package com.cloud.nest.fm.service;

import com.cloud.nest.db.fm.tables.records.FileRecord;
import com.cloud.nest.db.fm.tables.records.SharedFileRecord;
import com.cloud.nest.fm.inout.request.FileMetaIn;
import com.cloud.nest.fm.inout.request.SharedFileDownloadIn;
import com.cloud.nest.fm.inout.request.SharedFileIn;
import com.cloud.nest.fm.inout.response.FileOut;
import com.cloud.nest.fm.inout.response.SharedFileOut;
import com.cloud.nest.fm.inout.response.UploadedFileOut;
import com.cloud.nest.fm.mapper.FileRecordMapper;
import com.cloud.nest.fm.model.DownloadedFile;
import com.cloud.nest.fm.model.FileFilter;
import com.cloud.nest.fm.model.SingleFileUpload;
import com.cloud.nest.fm.persistence.repository.FileRepository;
import com.cloud.nest.fm.persistence.s3.S3FileStorage;
import com.cloud.nest.fm.util.FileUtils;
import com.cloud.nest.fm.util.FileUtils.Filename2Ext;
import com.cloud.nest.platform.infrastructure.streaming.ContentRangeSelection;
import com.cloud.nest.platform.infrastructure.streaming.ContentStreamingResponse;
import com.cloud.nest.platform.infrastructure.streaming.FileChunk;
import com.cloud.nest.platform.model.exception.DataNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static com.cloud.nest.fm.service.FileSharingService.SHARED_FILE_NOT_FOUND;
import static com.cloud.nest.fm.util.FileUtils.getFilenameAndExt;
import static com.cloud.nest.fm.util.MediaTypeMapper.getMediaTypeForFileExtension;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@Log4j2
@Service
@RequiredArgsConstructor
public class FileService implements BaseFileService {

    public static final String FILE_NOT_FOUND_ERROR = "File with id [%d] not found";
    public static final int RECORDS_FETCH_LIMIT = 100;

    private final UserStorageService userStorageService;
    private final FileSharingService fileSharingService;
    private final TransactionTemplate transactionTemplate;
    private final FileRepository fileRepository;
    private final S3FileStorage fileStorage;
    private final FileRecordMapper fileRecordMapper;

    @Transactional
    public UploadedFileOut uploadFile(Long userId, SingleFileUpload fileUpload) {
        userStorageService.createUserStorageIfNeeded(userId);
        userStorageService.checkUserStorageForUpload(userId, fileUpload.getSize());

        final FileItemInput itemInput = fileUpload.getFileInput();
        final Filename2Ext filename2Ext = FileUtils.truncate(getFilenameAndExt(itemInput.getName()));
        final String s3ObjectKey = fileStorage.uploadFile(
                userId,
                fileUpload,
                Map.of(
                        S3FileStorage.FILENAME_META, filename2Ext.filename(),
                        S3FileStorage.FILE_EXT_META, defaultIfEmpty(filename2Ext.ext(), EMPTY),
                        S3FileStorage.AUTHOR_USER_ID, String.valueOf(userId)
                )
        );

        final var now = LocalDateTime.now();
        final FileRecord fileRecord = fileRecordMapper.toRecord(
                s3ObjectKey,
                userId,
                filename2Ext,
                fileUpload.getSize(),
                itemInput.getContentType() != null
                        ? itemInput.getContentType()
                        : getMediaTypeForFileExtension(filename2Ext.ext()).toString(),
                false,
                now
        );
        fileRepository.save(fileRecord);
        return fileRecordMapper.toUploadedOut(fileRecord);
    }

    @Transactional
    public void updateFileMeta(@NotNull Long userId, @NotNull Long fileId, @NotNull FileMetaIn in) {
        final FileRecord fileRecord = getUserFile(userId, fileId);
        fileRecord.setFilename(in.filename());
        fileRecord.setExt(in.ext());
        fileRecord.setContentType(getMediaTypeForFileExtension(in.ext()).toString());
        fileRepository.save(fileRecord);
    }

    @Transactional
    @Override
    public void deleteFilesByIds(@NotNull Long userId, @NotNull Set<Long> fileIds) {
        final List<FileRecord> records = getUserFiles(userId, fileIds);
        records.forEach(r -> r.setDeleted(true));
        fileRepository.save(records);

        fileSharingService.deactivateAllSharesByFileIds(fileIds);

        final Long filesSize = records.stream()
                .map(FileRecord::getSize)
                .reduce(0L, Long::sum);

        userStorageService.updateUsedStorage(userId, filesSize);

        CompletableFuture.runAsync(() -> records.forEach(r ->
                fileStorage.deleteFile(r.getS3ObjectKey())
        ));
    }

    @Transactional(readOnly = true)
    @Override
    public List<FileOut> getFilesByUserId(
            @NotNull Long userId,
            @NotNull FileFilter criteria,
            int offset, int limit
    ) {
        if (Math.abs(offset - limit) > RECORDS_FETCH_LIMIT) {
            throw new IllegalArgumentException("Records fetch limit is exceeded");
        }
        return fileRepository.findAllByUserId(userId, criteria, offset, limit)
                .stream()
                .map(fileRecordMapper::toOut)
                .toList();
    }

    @Transactional
    public SharedFileOut shareFile(@NotNull Long userId, @NotNull Long fileId, @NotNull SharedFileIn in) {
        final FileRecord fileRecord = getUserFile(userId, fileId);
        return fileSharingService.shareFile(fileRecord, in);
    }

    @Transactional(readOnly = true)
    public List<SharedFileOut> getAllSharedFilesByFileId(@NotNull Long userId, @NotNull Long fileId) {
        getUserFile(userId, fileId);
        return fileSharingService.getAllSharesByFileId(fileId);
    }

    @Transactional
    public void deleteSharedFile(@NotNull Long userId, @NotNull UUID shareId) {
        final SharedFileRecord sharedFileRecord = fileSharingService.getSharedFileByShareId(shareId);
        final Optional<FileRecord> optFileRecord = fileRepository.findById(sharedFileRecord.getFileId());

        if (optFileRecord.isPresent() && isUserFile(userId, optFileRecord.get())) {
            fileSharingService.deactivateSharedFile(sharedFileRecord);
        } else {
            throw new DataNotFoundException(SHARED_FILE_NOT_FOUND.formatted(shareId));
        }
    }

    @Transactional
    public DownloadedFile downloadFileByShareId(
            @NotNull Long userId,
            @NotNull UUID shareId,
            @NotNull SharedFileDownloadIn in
    ) {
        final Long fileId = fileSharingService.getSharedFile(shareId, in);
        final Optional<FileRecord> optFileRecord = fileRepository.findById(fileId);

        if (optFileRecord.isPresent()) {
            final FileRecord fileRecord = optFileRecord.get();
            if (fileRecord.getDeleted()) {
                throw new DataNotFoundException(SHARED_FILE_NOT_FOUND.formatted(shareId));
            }

            final InputStream is = fileStorage.downloadFile(fileRecord.getS3ObjectKey());

            fileSharingService.incrementShareDownload(shareId);
            userStorageService.updateTotalDownloadedBytes(userId, fileRecord.getSize());

            return DownloadedFile.builder()
                    .name(FileUtils.concatFilenameAndExt(fileRecord.getFilename(), fileRecord.getExt()))
                    .contentType(MediaType.parseMediaType(fileRecord.getContentType()))
                    .size(fileRecord.getSize())
                    .resource(new InputStreamResource(is))
                    .build();
        }
        throw new DataNotFoundException(SHARED_FILE_NOT_FOUND.formatted(shareId));
    }

    @Transactional
    public FileChunk downloadFilePartByShareId(
            @NotNull Long userId,
            @NotNull UUID shareId,
            @NotNull ContentRangeSelection range,
            @NotNull SharedFileDownloadIn in
    ) {
        final Long fileId = fileSharingService.getSharedFile(shareId, in);
        final FileRecord fileRecord = fileRepository.findById(fileId)
                .orElseThrow(() -> new DataNotFoundException(SHARED_FILE_NOT_FOUND.formatted(shareId)));

        if (fileRecord.getDeleted()) {
            throw new DataNotFoundException(SHARED_FILE_NOT_FOUND.formatted(shareId));
        }

        checkRangeBounds(range, fileRecord);

        final StreamingResponseBody streamingBody = ContentStreamingResponse.builder()
                .inputStreamProvider(() -> fileStorage.downloadFileChunk(fileRecord.getS3ObjectKey(), range))
                .onReadFinished(readBytes -> handleFileChunkRead(readBytes, userId))
                .onError(handleFileStreamingError(userId, fileId, range))
                .build();

        fileSharingService.incrementShareDownload(shareId);

        return FileChunk.builder()
                .chunkSize(range.getContentLength())
                .fileSize(fileRecord.getSize())
                .contentType(MediaType.parseMediaType(fileRecord.getContentType()))
                .rangeSelection(range)
                .streamingBody(streamingBody)
                .build();
    }

    @Transactional
    public DownloadedFile downloadUserFile(@NotNull Long ownerUserId, @NotNull Long fileId) {
        final Optional<FileRecord> optFileRecord = fileRepository.findById(fileId);
        if (optFileRecord.isEmpty() ||
            !isUserFile(ownerUserId, optFileRecord.get()) ||
            optFileRecord.get().getDeleted()
        ) {
            throw new DataNotFoundException(FILE_NOT_FOUND_ERROR.formatted(fileId));
        }
        final FileRecord fileRecord = optFileRecord.get();
        final InputStream is = fileStorage.downloadFile(fileRecord.getS3ObjectKey());

        userStorageService.updateTotalDownloadedBytes(ownerUserId, fileRecord.getSize());

        return DownloadedFile.builder()
                .name(FileUtils.concatFilenameAndExt(fileRecord.getFilename(), fileRecord.getExt()))
                .contentType(MediaType.parseMediaType(fileRecord.getContentType()))
                .size(fileRecord.getSize())
                .resource(new InputStreamResource(is))
                .build();
    }

    @Transactional(readOnly = true)
    @NotNull
    @Override
    public FileRecord getUserFile(Long userId, Long fileId) {
        return fileRepository.findById(fileId)
                .map(foundRecord -> {
                    if (foundRecord.getDeleted() || !isUserFile(userId, foundRecord)) {
                        throw new DataNotFoundException(FILE_NOT_FOUND_ERROR.formatted(fileId));
                    }
                    return foundRecord;
                })
                .orElseThrow(() -> new DataNotFoundException(FILE_NOT_FOUND_ERROR.formatted(fileId)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<FileRecord> getUserFiles(Long userId, Set<Long> fileIds) {
        final List<FileRecord> fileRecords = fileRepository.findByIds(fileIds);
        fileRecords.stream()
                .filter(r -> r.getDeleted() || !isUserFile(userId, r))
                .findAny()
                .ifPresent(foundRecord -> {
                    throw new DataNotFoundException(FILE_NOT_FOUND_ERROR.formatted(foundRecord.getId()));
                });

        return fileRecords;
    }

    @Transactional(readOnly = true)
    public FileChunk downloadFilePart(Long ownerUserId, Long fileId, ContentRangeSelection range) {
        final FileRecord fileRecord = getUserFile(ownerUserId, fileId);
        checkRangeBounds(range, fileRecord);

        final StreamingResponseBody streamingBody = ContentStreamingResponse.builder()
                .inputStreamProvider(() -> fileStorage.downloadFileChunk(fileRecord.getS3ObjectKey(), range))
                .onReadFinished(readBytes -> handleFileChunkRead(readBytes, ownerUserId))
                .onError(handleFileStreamingError(ownerUserId, fileId, range))
                .build();

        return FileChunk.builder()
                .chunkSize(range.getContentLength())
                .fileSize(fileRecord.getSize())
                .contentType(MediaType.parseMediaType(fileRecord.getContentType()))
                .rangeSelection(range)
                .streamingBody(streamingBody)
                .build();
    }

    private boolean isUserFile(Long userId, @NotNull FileRecord record) {
        return record.getUploadedBy().equals(userId);
    }

    private void handleFileChunkRead(long readBytes, long userId) {
        if (readBytes != 0) {
            transactionTemplate.executeWithoutResult(ts -> userStorageService.updateTotalDownloadedBytes(
                    userId,
                    readBytes
            ));
        }
    }

    private BiConsumer<Long, Exception> handleFileStreamingError(Long userId, Long fileId, ContentRangeSelection range) {
        return (readBytes, err) -> log.debug(
                "Error occurred while reading file [{}] chunk [{}] by user [{}]. Read bytes: {}",
                fileId, range, userId, readBytes
        );
    }

    private void checkRangeBounds(ContentRangeSelection range, FileRecord fileRecord) {
        if (range.getEndByte() == null || range.getEndByte() > fileRecord.getSize()) {
            range.setEndByte(fileRecord.getSize());
        }
    }
}

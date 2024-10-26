package com.cloud.nest.fm.service;

import com.cloud.nest.db.fm.tables.records.FileRecord;
import com.cloud.nest.fm.inout.FileMetaIn;
import com.cloud.nest.fm.inout.FileOut;
import com.cloud.nest.fm.inout.UploadedFileOut;
import com.cloud.nest.fm.mapper.FileRecordMapper;
import com.cloud.nest.fm.persistence.repository.FileRepository;
import com.cloud.nest.fm.persistence.s3.S3FileStorage;
import com.cloud.nest.fm.util.FileUtils;
import com.cloud.nest.fm.util.FileUtils.Filename2Ext;
import com.cloud.nest.platform.model.exception.DataNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.cloud.nest.fm.util.FileUtils.getFilenameAndExt;
import static com.cloud.nest.fm.util.MediaTypeMapper.getMediaTypeForFileExtension;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@Service
@RequiredArgsConstructor
public class FileService {

    public static final String FILE_NOT_FOUND_ERROR = "File with id [%d] not found";
    public static final int RECORDS_FETCH_LIMIT = 100;

    private final UserStorageService userStorageService;
    private final FileRepository fileRepository;
    private final S3FileStorage fileStorage;
    private final FileRecordMapper fileRecordMapper;

    @Transactional
    public UploadedFileOut uploadFile(@NotNull Long userId, @NotNull MultipartFile file) {
        userStorageService.createUserStorageIfNeeded(userId);
        userStorageService.checkUserStorage(userId, file.getSize());

        final String filename = file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : file.getName();

        final Filename2Ext filename2Ext = FileUtils.truncate(getFilenameAndExt(filename));
        final String s3ObjectKey = fileStorage.uploadFile(
                userId,
                file,
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
                file.getSize(),
                file.getContentType() != null
                        ? file.getContentType()
                        : getMediaTypeForFileExtension(filename2Ext.ext()).toString(),
                false,
                now
        );
        fileRepository.save(fileRecord);
        return fileRecordMapper.toUploadedOut(fileRecord);
    }

    @Transactional
    public void updateFileMeta(@NotNull Long userId, @NotNull Long fileId, @NotNull FileMetaIn in) {
        fileRepository.findById(fileId).ifPresentOrElse(
                foundRecord -> {
                    if (!foundRecord.getUploadedBy().equals(userId)) {
                        throw new DataNotFoundException(FILE_NOT_FOUND_ERROR.formatted(fileId));
                    }
                    foundRecord.setFilename(in.filename());
                    foundRecord.setExt(in.ext());
                    foundRecord.setContentType(getMediaTypeForFileExtension(in.ext()).toString());
                    fileRepository.save(foundRecord);
                },
                () -> {
                    throw new DataNotFoundException(FILE_NOT_FOUND_ERROR.formatted(fileId));
                }
        );
    }

    @Transactional(readOnly = true)
    public List<FileOut> getFilesByUserId(@NotNull Long userId, int offset, int limit) {
        if (Math.abs(offset - limit) > RECORDS_FETCH_LIMIT) {
            throw new IllegalArgumentException("Records fetch limit is exceeded");
        }
        return fileRepository.findAllByUserId(userId, offset, limit)
                .stream()
                .map(fileRecordMapper::toOut)
                .toList();
    }

}

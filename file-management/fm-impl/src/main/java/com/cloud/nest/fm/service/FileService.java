package com.cloud.nest.fm.service;

import com.cloud.nest.db.fm.tables.records.FileRecord;
import com.cloud.nest.fm.mapper.FileRecordMapper;
import com.cloud.nest.fm.persistence.repository.FileRepository;
import com.cloud.nest.fm.persistence.s3.S3FileStorage;
import com.cloud.nest.fm.util.FileUtils;
import com.cloud.nest.fm.util.FileUtils.Filename2Ext;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

import static com.cloud.nest.fm.util.FileUtils.getFilenameAndExt;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UserStorageService userStorageService;
    private final FileRepository fileRepository;
    private final S3FileStorage fileStorage;
    private final FileRecordMapper fileRecordMapper;

    @Transactional
    public long uploadFile(@NotNull Long userId, @NotNull MultipartFile file) {
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
                false,
                now
        );
        fileRepository.save(fileRecord);
        return fileRecord.getId();
    }

}

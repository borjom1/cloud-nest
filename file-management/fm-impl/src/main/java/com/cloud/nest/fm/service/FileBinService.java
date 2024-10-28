package com.cloud.nest.fm.service;

import com.cloud.nest.db.fm.tables.records.FileRecord;
import com.cloud.nest.fm.config.StorageProperties;
import com.cloud.nest.fm.inout.response.FileBinOut;
import com.cloud.nest.fm.mapper.FileRecordMapper;
import com.cloud.nest.fm.persistence.repository.FileRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Log4j2
@Service
@RequiredArgsConstructor
public class FileBinService {

    public static final int RECORDS_FETCH_LIMIT = 100;

    private final BaseFileService fileService;
    private final FileRepository fileRepository;
    private final StorageProperties storageProperties;
    private final FileRecordMapper fileRecordMapper;

    @Scheduled(fixedRate = 30L, timeUnit = TimeUnit.MINUTES)
    @Transactional
    void clearBin() {
        log.info("Start clearing bin...");

        final int deletedFiles = fileRepository.setDeletedForAllWithPlacedToBinLaterThanDays(
                storageProperties.getBin()
                        .getClearRate()
                        .toDays()
        );

        log.info("Total count of files deleted from bin: {}", deletedFiles);
    }

    @Transactional
    public void addFileToBin(@NotNull Long userId, @NotNull Long fileId) {
        changeFileRecordAndUpdate(
                fileService.getUserFile(userId, fileId),
                record -> record.setPlacedToBin(LocalDateTime.now())
        );
    }

    @Transactional
    public void deleteFileFromBin(@NotNull Long userId, @NotNull Long fileId) {
        changeFileRecordAndUpdate(
                fileService.getUserFile(userId, fileId),
                record -> record.setPlacedToBin(null)
        );
    }

    @Transactional(readOnly = true)
    public List<FileBinOut> getBinFiles(Long userId, int offset, int limit) {
        if (Math.abs(offset - limit) > RECORDS_FETCH_LIMIT) {
            throw new IllegalArgumentException("Records fetch limit is exceeded");
        }
        return fileRepository.findAllPlacedToBinByUserId(userId, offset, limit)
                .stream()
                .map(fileRecordMapper::toBinOut)
                .toList();
    }

    private void changeFileRecordAndUpdate(
            @NotNull FileRecord record,
            @NotNull Consumer<FileRecord> action
    ) {
        action.accept(record);
        fileRepository.save(record);
    }

}

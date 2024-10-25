package com.cloud.nest.fm.service;

import com.cloud.nest.db.fm.tables.records.UserStorageRecord;
import com.cloud.nest.fm.config.StorageProperties;
import com.cloud.nest.fm.exception.OperationNotAllowedException;
import com.cloud.nest.fm.exception.StorageSpaceLimitExceededException;
import com.cloud.nest.fm.persistence.repository.UserStorageRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserStorageService {

    private final UserStorageRepository userStorageRepository;
    private final StorageProperties storageProperties;

    @Transactional
    public void checkUserStorage(@NotNull Long userId, @NotNull Long uploadingFileSize) {
        final UserStorageRecord record = findOrCreateUserStorage(userId);
        if (record.getDisabled()) {
            throw new OperationNotAllowedException("File upload is not allowed");
        }

        final long updatedUsedStorage = record.getUsedStorageSpace() + uploadingFileSize;
        if (updatedUsedStorage > record.getStorageSpace()) {
            throw new StorageSpaceLimitExceededException(String.format(
                    "Uploading file exceeds the storage space [%d] bytes",
                    record.getUsedStorageSpace()
            ));
        }
        record.setUsedStorageSpace(updatedUsedStorage);
        record.setTotalUploadedBytes(record.getTotalUploadedBytes() + uploadingFileSize);
        userStorageRepository.update(record);
    }

    @NotNull
    public UserStorageRecord findOrCreateUserStorage(@NotNull Long userId) {
        return userStorageRepository
                .findByUserId(userId)
                .orElseGet(() -> createUserStorageRecord(userId));
    }

    @NotNull
    private UserStorageRecord createUserStorageRecord(@NotNull Long userId) {
        final LocalDateTime now = LocalDateTime.now();

        final var record = new UserStorageRecord();
        record.setUserId(userId);
        record.setCreated(now);
        record.setUpdated(now);

        record.setDisabled(Boolean.FALSE);

        record.setStorageSpace(storageProperties.getUser().getDefaultStorageSize());
        record.setUsedStorageSpace(0L);
        record.setTotalUploadedBytes(0L);
        record.setTotalDownloadedBytes(0L);

        userStorageRepository.insert(record);
        return record;
    }

}
package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.UserStorageRecord;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface UserStorageRepository {
    void insert(@NotNull UserStorageRecord record);

    void update(@NotNull UserStorageRecord record);

    Optional<UserStorageRecord> findByUserId(Long userId);
}

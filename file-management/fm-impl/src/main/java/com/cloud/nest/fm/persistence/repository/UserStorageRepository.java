package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.UserStorageRecord;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface UserStorageRepository {
    void insert(@NotNull UserStorageRecord record);

    void update(@NotNull UserStorageRecord record);

    UserStorageRecord findByUserIdForUpdate(Long userId);

    boolean existsByUserId(Long userId);

    @NotNull
    List<UserStorageRecord> findAll(@Nullable Long lastUserId, long limit);
}

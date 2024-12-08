package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.SharedFileRecord;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SharedFileRepository {
    void insert(@NotNull SharedFileRecord record);

    void update(@NotNull SharedFileRecord record);

    Optional<SharedFileRecord> findNotExpiredById(UUID uuid);

    Optional<SharedFileRecord> findByIdForUpdate(UUID uuid);

    @NotNull
    List<SharedFileRecord> findAllNotExpiredByFileId(Long fileId);

    void updateExpirationForAllNotExpiredByFileIds(@NotNull Set<Long> fileIds, @NotNull LocalDateTime expiresAt);

    long countNotExpiredSharesByFileId(Long fileId);
}

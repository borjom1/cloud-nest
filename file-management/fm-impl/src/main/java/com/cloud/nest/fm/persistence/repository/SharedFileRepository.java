package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.SharedFileRecord;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SharedFileRepository {
    void insert(@NotNull SharedFileRecord record);

    void update(@NotNull SharedFileRecord record);

    Optional<SharedFileRecord> findById(UUID uuid);

    @NotNull
    List<SharedFileRecord> findAllByFileId(Long fileId);
}
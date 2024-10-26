package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.FileRecord;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface FileRepository {
    FileRecord save(@NotNull FileRecord record);

    Optional<FileRecord> findById(Long id);

    List<FileRecord> findAllByUserId(Long userId, int offset, int limit);
}

package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.FileRecord;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public interface FileRepository {
    FileRecord save(@NotNull FileRecord record);

    @Nullable
    FileRecord findById(Long id);
}

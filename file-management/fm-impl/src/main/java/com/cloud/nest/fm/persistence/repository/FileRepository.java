package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.FileRecord;
import com.cloud.nest.fm.model.FileFilter;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface FileRepository {

    @NotNull
    FileRecord save(@NotNull FileRecord record);

    Optional<FileRecord> findById(Long id);

    List<FileRecord> findAllByUserId(Long userId, @NotNull FileFilter criteria, int offset, int limit);

    List<FileRecord> findAllPlacedToBinByUserId(Long userId, int offset, int limit);

    int setDeletedForAllWithPlacedToBinLaterThanDays(long days);
}

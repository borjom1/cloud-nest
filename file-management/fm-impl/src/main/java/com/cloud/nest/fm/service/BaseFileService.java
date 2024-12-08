package com.cloud.nest.fm.service;

import com.cloud.nest.db.fm.tables.records.FileRecord;
import com.cloud.nest.fm.inout.response.FileOut;
import com.cloud.nest.fm.model.FileFilter;
import com.cloud.nest.platform.model.exception.DataNotFoundException;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

public interface BaseFileService {
    void deleteFilesByIds(@NotNull Long userId, Set<Long> fileId);

    @NotNull
    FileRecord getUserFile(Long userId, Long fileId) throws DataNotFoundException;

    List<FileRecord> getUserFiles(Long userId, Set<Long> fileId) throws DataNotFoundException;

    @NotNull
    List<FileOut> getFilesByUserId(@NotNull Long userId, @NotNull FileFilter criteria, int offset, int limit);
}

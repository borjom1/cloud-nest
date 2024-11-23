package com.cloud.nest.fm.service;

import com.cloud.nest.db.fm.tables.records.FileRecord;
import com.cloud.nest.fm.inout.response.FileOut;
import com.cloud.nest.fm.model.FileSearchCriteria;
import com.cloud.nest.platform.model.exception.DataNotFoundException;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface BaseFileService {
    void deleteFile(@NotNull Long userId, @NotNull Long fileId);

    @NotNull
    FileRecord getUserFile(Long userId, Long fileId) throws DataNotFoundException;

    @NotNull
    List<FileOut> getFilesByUserId(@NotNull Long userId, @NotNull FileSearchCriteria criteria, int offset, int limit);
}

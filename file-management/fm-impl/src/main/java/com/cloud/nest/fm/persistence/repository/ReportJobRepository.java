package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.ReportJobRecord;
import com.cloud.nest.fm.model.ReportType;
import jakarta.annotation.Nullable;

public interface ReportJobRepository {
    ReportJobRecord save(ReportJobRecord record);

    @Nullable
    ReportJobRecord findLastByReportType(ReportType type);
}

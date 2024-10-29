package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.ReportRecord;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface ReportRepository {
    void save(@NotNull ReportRecord record);

    @NotNull
    List<ReportRecord> findAllWeeklyByUserId(Long userId);

    @NotNull
    List<ReportRecord> findAllMonthlyByUserId(Long userId);
}

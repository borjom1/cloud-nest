package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.ReportRecord;
import com.cloud.nest.fm.inout.response.UserReportOut;
import com.cloud.nest.fm.model.ReportType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface ReportRepository {
    void save(@NotNull ReportRecord record);

    List<UserReportOut> findAllByUserIdAndType(Long userId, ReportType reportType);

    default List<UserReportOut> findAllWeeklyByUserId(Long userId) {
        return findAllByUserIdAndType(userId, ReportType.WEEKLY);
    }

    default List<UserReportOut> findAllMonthlyByUserId(Long userId) {
        return findAllByUserIdAndType(userId, ReportType.MONTHLY);
    }
}

package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.ReportJobRecord;
import com.cloud.nest.db.fm.tables.records.ReportRecord;
import com.cloud.nest.fm.inout.response.UserReportOut;
import com.cloud.nest.fm.model.ReportType;
import com.cloud.nest.platform.model.exception.TransactionFailedException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.cloud.nest.db.fm.Tables.REPORT_JOB;
import static com.cloud.nest.db.fm.tables.Report.REPORT;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {

    private static final String CREATE_REPORT_ERROR = "Cannot create report record: %s";
    private static final String UPDATE_REPORT_ERROR = "Cannot update report record with id = %d";

    private final DSLContext dsl;

    @Transactional(propagation = MANDATORY)
    @NotNull
    @Override
    public void save(@NotNull ReportRecord record) {
        boolean isNew = ObjectUtils.isEmpty(record.getId());
        try {
            if (isNew) {
                dsl.executeInsert(record);
            } else {
                dsl.executeUpdate(record);
            }
        } catch (DataAccessException e) {
            String errorMessage = isNew
                    ? CREATE_REPORT_ERROR.formatted(record)
                    : UPDATE_REPORT_ERROR.formatted(record.getId());
            throw new TransactionFailedException(errorMessage, e);
        }
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @NotNull
    @Override
    public List<UserReportOut> findAllByUserIdAndType(Long userId, ReportType reportType) {
        return dsl.select(REPORT, REPORT_JOB)
                .from(REPORT)
                .join(REPORT_JOB)
                .on(REPORT.REPORT_JOB_ID.eq(REPORT_JOB.ID))
                .where(REPORT.USER_ID.eq(userId).and(
                        REPORT_JOB.TYPE.eq(reportType.name())
                ))
                .orderBy(REPORT.CREATED)
                .stream()
                .map(r -> toUserReportOut(r, reportType))
                .toList();
    }

    private static UserReportOut toUserReportOut(Record2<ReportRecord, ReportJobRecord> r, ReportType reportType) {
        final ReportRecord reportRecord = r.component1();
        return UserReportOut.builder()
                .uploadedBytes(reportRecord.getUploadedBytes())
                .downloadedBytes(reportRecord.getDownloadedBytes())
                .periodStart(r.component2().getCreated().toLocalDate())
                .periodEnd(r.component2().getCreated().minus(reportType.getPeriod()).toLocalDate())
                .created(reportRecord.getCreated())
                .build();
    }

}

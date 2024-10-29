package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.ReportRecord;
import com.cloud.nest.platform.model.exception.TransactionFailedException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.cloud.nest.db.fm.tables.Report.REPORT;
import static org.jooq.impl.DSL.localDateTimeDiff;
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
    public List<ReportRecord> findAllWeeklyByUserId(Long userId) {
        return dsl.selectFrom(REPORT)
                .where(REPORT.USER_ID.eq(userId).and(
                        localDateTimeDiff(DatePart.DAY, REPORT.PERIOD_START, REPORT.PERIOD_END).eq(7)
                ))
                .orderBy(REPORT.CREATED)
                .fetchInto(ReportRecord.class);
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @NotNull
    @Override
    public List<ReportRecord> findAllMonthlyByUserId(Long userId) {
        return dsl.selectFrom(REPORT)
                .where(REPORT.USER_ID.eq(userId).and(
                        localDateTimeDiff(DatePart.DAY, REPORT.PERIOD_START, REPORT.PERIOD_END).eq(30)
                ))
                .orderBy(REPORT.CREATED)
                .fetchInto(ReportRecord.class);
    }

}

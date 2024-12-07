package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.ReportJobRecord;
import com.cloud.nest.fm.model.ReportType;
import com.cloud.nest.platform.model.exception.TransactionFailedException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

import static com.cloud.nest.db.fm.Tables.REPORT_JOB;

@Repository
@RequiredArgsConstructor
public class ReportJobRepositoryImpl implements ReportJobRepository {

    private static final String CREATE_RECORD_ERROR = "Cannot create report job: %s";
    private static final String UPDATE_RECORD_ERROR = "Cannot update report job with id = %s";

    private final DSLContext dsl;

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public ReportJobRecord save(ReportJobRecord record) {
        final boolean isNew = ObjectUtils.isEmpty(record.getId());
        try {
            if (isNew) {
                final Long id = dsl.insertInto(REPORT_JOB)
                        .columns(REPORT_JOB.TYPE, REPORT_JOB.LAST_REPORTED_USER_ID, REPORT_JOB.CREATED,
                                REPORT_JOB.UPDATED, REPORT_JOB.COMPLETED)
                        .values(record.getType(), record.getLastReportedUserId(), record.getCreated(),
                                record.getUpdated(), record.getCompleted())
                        .returning(REPORT_JOB.ID)
                        .fetchOne(REPORT_JOB.ID);
                record.setId(id);
            } else {
                record.setUpdated(LocalDateTime.now());
                dsl.executeUpdate(record);
            }
            return record;
        } catch (DataAccessException e) {
            String errorMessage = isNew
                    ? CREATE_RECORD_ERROR.formatted(record)
                    : UPDATE_RECORD_ERROR.formatted(record.getId());
            throw new TransactionFailedException(errorMessage, e);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Nullable
    @Override
    public ReportJobRecord findLastByReportType(ReportType type) {
        return dsl.selectFrom(REPORT_JOB)
                .where(REPORT_JOB.TYPE.eq(type.name()))
                .orderBy(REPORT_JOB.CREATED.desc())
                .limit(1)
                .fetchOne();
    }

}

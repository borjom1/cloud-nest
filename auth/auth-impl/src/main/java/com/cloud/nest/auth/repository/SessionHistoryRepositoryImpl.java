package com.cloud.nest.auth.repository;

import com.cloud.nest.db.auth.tables.records.SessionHistoryRecord;
import com.cloud.nest.platform.model.exception.TransactionFailedException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.cloud.nest.db.auth.Tables.SESSION_HISTORY;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
public class SessionHistoryRepositoryImpl implements SessionHistoryRepository {

    private static final String CREATE_RECORD_ERROR = "Cannot create session history: %s";
    private static final String UPDATE_RECORD_ERROR = "Cannot update session history with session_id = %s and user_id = %d";

    private final DSLContext dsl;

    @Transactional(propagation = MANDATORY)
    @Override
    public void insert(@NotNull SessionHistoryRecord record) {
        try {
            dsl.executeInsert(record);
        } catch (DataAccessException e) {
            throw new TransactionFailedException(CREATE_RECORD_ERROR.formatted(record), e);
        }
    }

    @Transactional(propagation = MANDATORY)
    @Override
    public void update(@NotNull SessionHistoryRecord record) {
        try {
            record.setCreated(LocalDateTime.now());
            dsl.executeUpdate(record);
        } catch (DataAccessException e) {
            throw new TransactionFailedException(
                    UPDATE_RECORD_ERROR.formatted(record.getSessionId(), record.getUserId()),
                    e
            );
        }
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Override
    public List<SessionHistoryRecord> findAllByUserIdOrderedByCreated(Long userId, int offset, int limit) {
        return dsl.selectFrom(SESSION_HISTORY)
                .where(SESSION_HISTORY.USER_ID.eq(userId))
                .orderBy(SESSION_HISTORY.CREATED.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    @Transactional(propagation = MANDATORY)
    @Override
    public void updateLastActiveBySessionIdAndUserId(String sessionId, Long userId, LocalDateTime now) {
        dsl.update(SESSION_HISTORY)
                .set(SESSION_HISTORY.LAST_ACTIVE, now)
                .where(SESSION_HISTORY.SESSION_ID.eq(sessionId).and(
                        SESSION_HISTORY.USER_ID.eq(userId)
                ))
                .execute();
    }

}

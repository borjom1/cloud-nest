package com.cloud.nest.auth.repository;

import com.cloud.nest.auth.inout.SessionStatus;
import com.cloud.nest.db.auth.tables.records.SessionRecord;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.cloud.nest.db.auth.Tables.SESSION;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryImpl implements SessionRepository {

    private final DSLContext dsl;

    @Transactional(propagation = MANDATORY)
    @Override
    public void insert(@NotNull SessionRecord record) {
        dsl.executeInsert(record);
    }

    @Transactional(propagation = MANDATORY)
    @Override
    public void update(@NotNull SessionRecord record) {
        dsl.executeUpdate(record);
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Nullable
    @Override
    public SessionRecord findById(@NotNull String id) {
        return dsl.selectFrom(SESSION)
                .where(SESSION.ID.eq(id))
                .fetchOne();
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Override
    public boolean existsActiveSession(String clientIp) {
        return dsl.fetchExists(
                SESSION,
                SESSION.CLIENT_IP.eq(clientIp)
                        .and(SESSION.STATUS.equalIgnoreCase(SessionStatus.ACTIVE.name()))
        );
    }

    @Transactional(propagation = MANDATORY)
    @Override
    public int deleteExpiredSessions() {
        return dsl.deleteFrom(SESSION)
                .where(SESSION.EXPIRES_AT.lessThan(LocalDateTime.now()))
                .execute();
    }

}

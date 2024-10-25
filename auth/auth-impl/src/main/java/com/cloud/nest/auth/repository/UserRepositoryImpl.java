package com.cloud.nest.auth.repository;

import com.cloud.nest.db.auth.tables.records.UserRecord;
import com.cloud.nest.platform.model.exception.TransactionFailedException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.cloud.nest.db.auth.Tables.USER;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final DSLContext dsl;

    @Transactional(propagation = MANDATORY)
    @Override
    public void insert(@NotNull UserRecord record) {
        try {
            dsl.executeInsert(record);
        } catch (DataAccessException e) {
            throw new TransactionFailedException("Cannot insert user with sessionId = %d".formatted(record.getId()), e);
        }
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Override
    public boolean existsById(long id) {
        return dsl.fetchExists(USER, USER.ID.eq(id));
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Nullable
    @Override
    public UserRecord findByUsername(String username) {
        return dsl.selectFrom(USER)
                .where(USER.USERNAME.equalIgnoreCase(username))
                .fetchOne();
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Nullable
    @Override
    public UserRecord findById(Long id) {
        return dsl.selectFrom(USER)
                .where(USER.ID.eq(id))
                .fetchOne();
    }

}

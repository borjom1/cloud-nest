package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.UserStorageRecord;
import com.cloud.nest.platform.model.exception.TransactionFailedException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.cloud.nest.db.fm.Tables.USER_STORAGE;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
public class UserStorageRepositoryImpl implements UserStorageRepository {

    private final DSLContext dsl;

    @Transactional(propagation = MANDATORY)
    @Override
    public void insert(@NotNull UserStorageRecord record) {
        try {
            dsl.executeInsert(record);
        } catch (DataAccessException e) {
            throw new TransactionFailedException(
                    "Failed to insert user rating record %s".formatted(record),
                    e
            );
        }
    }

    @Transactional(propagation = MANDATORY)
    @Override
    public void update(@NotNull UserStorageRecord record) {
        try {
            record.setUpdated(LocalDateTime.now());
            dsl.executeUpdate(record);
        } catch (DataAccessException e) {
            throw new TransactionFailedException(
                    "Failed to update user rating record with user_id = %d".formatted(record.getUserId()),
                    e
            );
        }
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Override
    public UserStorageRecord findByUserIdForUpdate(Long userId) {
        return dsl.selectFrom(USER_STORAGE)
                .where(USER_STORAGE.USER_ID.eq(userId))
                .forUpdate()
                .fetchOne();
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Override
    public boolean existsByUserId(Long userId) {
        return dsl.fetchExists(USER_STORAGE, USER_STORAGE.USER_ID.eq(userId));
    }

}

package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.SharedFileRecord;
import com.cloud.nest.platform.model.exception.TransactionFailedException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.cloud.nest.db.fm.Tables.SHARED_FILE;
import static java.time.LocalDateTime.now;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
public class SharedFileRepositoryImpl implements SharedFileRepository {

    private final DSLContext dsl;

    @Transactional(propagation = MANDATORY)
    @Override
    public void insert(@NotNull SharedFileRecord record) {
        try {
            dsl.executeInsert(record);
        } catch (DataAccessException e) {
            throw new TransactionFailedException(
                    "Failed to insert shared file record %s".formatted(record),
                    e
            );
        }
    }

    @Transactional(propagation = MANDATORY)
    @Override
    public void update(@NotNull SharedFileRecord record) {
        try {
            record.setUpdated(now());
            dsl.executeUpdate(record);
        } catch (DataAccessException e) {
            throw new TransactionFailedException(
                    "Failed to update shared file record with id = %s".formatted(record.getId()),
                    e
            );
        }
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Override
    public Optional<SharedFileRecord> findNotExpiredById(UUID uuid) {
        return Optional.ofNullable(
                dsl.selectFrom(SHARED_FILE)
                        .where(SHARED_FILE.ID.eq(uuid).and(
                                SHARED_FILE.EXPIRES_AT
                                        .greaterOrEqual(now())
                                        .or(SHARED_FILE.EXPIRES_AT.isNull())
                        ))
                        .fetchOne()
        );
    }

    @Transactional(propagation = MANDATORY)
    @Override
    public Optional<SharedFileRecord> findByIdForUpdate(UUID uuid) {
        return Optional.ofNullable(
                dsl.selectFrom(SHARED_FILE)
                        .where(SHARED_FILE.ID.eq(uuid))
                        .forUpdate()
                        .fetchOne()
        );
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @NotNull
    @Override
    public List<SharedFileRecord> findAllNotExpiredByFileId(Long fileId) {
        return dsl.selectFrom(SHARED_FILE)
                .where(SHARED_FILE.FILE_ID.eq(fileId).and(
                        SHARED_FILE.EXPIRES_AT.greaterOrEqual(now()).or(SHARED_FILE.EXPIRES_AT.isNull())
                ))
                .fetchInto(SharedFileRecord.class);
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Override
    public long countNotExpiredSharesByFileId(Long fileId) {
        final var now = now();
        return dsl.fetchCount(
                SHARED_FILE,
                SHARED_FILE.FILE_ID.eq(fileId).and(
                        SHARED_FILE.EXPIRES_AT.greaterOrEqual(now).or(SHARED_FILE.EXPIRES_AT.isNull())
                )
        );
    }

}

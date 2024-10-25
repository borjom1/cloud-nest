package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.SharedFileRecord;
import com.cloud.nest.platform.model.exception.TransactionFailedException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.cloud.nest.db.fm.Tables.SHARED_FILE;
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
            record.setUpdated(LocalDateTime.now());
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
    public Optional<SharedFileRecord> findById(UUID uuid) {
        return Optional.ofNullable(
                dsl.selectFrom(SHARED_FILE)
                        .where(SHARED_FILE.ID.eq(uuid))
                        .fetchOne()
        );
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @NotNull
    @Override
    public List<SharedFileRecord> findAllByFileId(Long fileId) {
        return dsl.selectFrom(SHARED_FILE)
                .where(SHARED_FILE.FILE_ID.eq(fileId))
                .fetchInto(SharedFileRecord.class);
    }

}

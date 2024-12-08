package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.FileRecord;
import com.cloud.nest.fm.model.FileFilter;
import com.cloud.nest.fm.util.filter.JooqQueryFilterTranslator;
import com.cloud.nest.platform.model.exception.TransactionFailedException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.cloud.nest.db.fm.Tables.FILE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.time.LocalDateTime.now;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepository {

    private static final String CREATE_FILE_ERROR = "Cannot create file: %s";
    private static final String UPDATE_FILE_ERROR = "Cannot update file with id = %s";

    private final DSLContext dsl;
    private final JooqQueryFilterTranslator translator;

    @Transactional(propagation = MANDATORY)
    @Override
    public FileRecord save(@NotNull FileRecord record) {
        final boolean isNew = ObjectUtils.isEmpty(record.getId());
        try {
            if (isNew) {
                Long id = dsl.insertInto(FILE)
                        .columns(
                                FILE.S3_OBJECT_KEY, FILE.FILENAME, FILE.EXT,
                                FILE.SIZE, FILE.UPLOADED_BY, FILE.CONTENT_TYPE,
                                FILE.DELETED, FILE.CREATED, FILE.UPDATED
                        )
                        .values(
                                record.getS3ObjectKey(), record.getFilename(), record.getExt(),
                                record.getSize(), record.getUploadedBy(), record.getContentType(),
                                record.getDeleted(), record.getCreated(), record.getUpdated()
                        )
                        .returning(FILE.ID)
                        .fetchOne(FILE.ID);
                record.setId(id);
            } else {
                record.setUpdated(now());
                dsl.executeUpdate(record);
            }
            return record;
        } catch (DataAccessException e) {
            String errorMessage = isNew
                    ? CREATE_FILE_ERROR.formatted(record)
                    : UPDATE_FILE_ERROR.formatted(record.getId());
            throw new TransactionFailedException(errorMessage, e);
        }

    }

    @Transactional(propagation = MANDATORY)
    @Override
    public List<FileRecord> save(@NotNull List<FileRecord> records) {
        final LocalDateTime now = now();
        records.forEach(r -> r.setUpdated(now));
        dsl.batchUpdate(records).execute();
        return records;
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Override
    public Optional<FileRecord> findById(Long id) {
        return Optional.ofNullable(
                dsl.selectFrom(FILE)
                        .where(FILE.ID.eq(id))
                        .fetchOne()
        );
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Override
    public List<FileRecord> findByIds(Set<Long> ids) {
        return dsl.selectFrom(FILE)
                .where(FILE.ID.in(ids))
                .fetchInto(FileRecord.class);
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Override
    public List<FileRecord> findAllByUserId(Long userId, @NotNull FileFilter criteria, int offset, int limit) {
        return dsl.selectFrom(FILE)
                .where(FILE.UPLOADED_BY.eq(userId).and(translator.toCondition(criteria)))
                .orderBy(FILE.CREATED.desc())
                .offset(offset)
                .limit(limit)
                .fetchInto(FileRecord.class);
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Override
    public List<FileRecord> findAllPlacedToBinByUserId(Long userId, int offset, int limit) {
        return dsl.selectFrom(FILE)
                .where(FILE.UPLOADED_BY.eq(userId).and(
                        FILE.DELETED.eq(FALSE).and(
                                FILE.PLACED_TO_BIN.isNotNull()
                        )
                ))
                .orderBy(FILE.PLACED_TO_BIN.desc())
                .offset(offset)
                .limit(limit)
                .fetchInto(FileRecord.class);
    }

    @Transactional(propagation = MANDATORY)
    @Override
    public int setDeletedForAllWithPlacedToBinLaterThanDays(long days) {
        return dsl.update(FILE)
                .set(FILE.DELETED, TRUE)
                .where(FILE.PLACED_TO_BIN.lessThan(now().minusDays(days)).and(
                        FILE.DELETED.eq(FALSE)
                ))
                .execute();
    }

}

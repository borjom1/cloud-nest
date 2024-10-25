package com.cloud.nest.fm.persistence.repository;

import com.cloud.nest.db.fm.tables.records.FileRecord;
import com.cloud.nest.platform.model.exception.TransactionFailedException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

import static com.cloud.nest.db.fm.Tables.FILE;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepository {

    private static final String CREATE_FILE_ERROR = "Cannot create file: %s";
    private static final String UPDATE_FILE_ERROR = "Cannot update file with id = %s";

    private final DSLContext dsl;

    @Transactional(propagation = MANDATORY)
    @Override
    public FileRecord save(@NotNull FileRecord record) {
        final boolean isNew = ObjectUtils.isEmpty(record.getId());
        try {
            if (isNew) {
                Long id = dsl.insertInto(FILE)
                        .columns(
                                FILE.S3_OBJECT_KEY, FILE.FILENAME, FILE.EXT, FILE.SIZE,
                                FILE.UPLOADED_BY, FILE.DELETED, FILE.CREATED, FILE.UPDATED
                        )
                        .values(
                                record.getS3ObjectKey(), record.getFilename(), record.getExt(),
                                record.getSize(), record.getUploadedBy(), record.getDeleted(),
                                record.getCreated(), record.getUpdated()
                        )
                        .returning(FILE.ID)
                        .fetchOne(FILE.ID);
                record.setId(id);
            } else {
                record.setUpdated(LocalDateTime.now());
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

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Nullable
    @Override
    public FileRecord findById(Long id) {
        return dsl.selectFrom(FILE)
                .where(FILE.ID.eq(id))
                .fetchOne();
    }

}

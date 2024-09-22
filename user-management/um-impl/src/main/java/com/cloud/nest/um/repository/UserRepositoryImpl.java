package com.cloud.nest.um.repository;

import com.cloud.nest.db.um.tables.records.UserRecord;
import com.cloud.nest.platform.model.exception.TransactionFailedException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import static com.cloud.nest.db.um.Tables.USER;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private static final String CREATE_USER_ERROR = "Cannot create user with id = %s";
    private static final String UPDATE_USER_ERROR = "Cannot update user with id = %s";

    private final DSLContext dsl;

    @Transactional(propagation = MANDATORY)
    @Override
    public void save(UserRecord record) {
        final boolean isNew = ObjectUtils.isEmpty(record.getId());
        try {
            if (isNew) {
                dsl.executeInsert(record);
            } else {
                dsl.executeUpdate(record);
            }
        } catch (DataAccessException e) {
            String errorMessage = isNew
                    ? CREATE_USER_ERROR.formatted(record.getId())
                    : UPDATE_USER_ERROR.formatted(record.getId());
            throw new TransactionFailedException(errorMessage, e);
        }
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

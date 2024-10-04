package com.cloud.nest.um.repository;

import com.cloud.nest.db.um.tables.records.UserRecord;
import com.cloud.nest.platform.model.exception.TransactionFailedException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

import static com.cloud.nest.db.um.Tables.USER;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private static final String CREATE_USER_ERROR = "Cannot create user: %s";
    private static final String UPDATE_USER_ERROR = "Cannot update user with id = %s";

    private final DSLContext dsl;

    @Transactional(propagation = MANDATORY)
    @Override
    public void save(@NotNull UserRecord record) {
        final boolean isNew = ObjectUtils.isEmpty(record.getId());
        try {
            if (isNew) {
                Long id = dsl.insertInto(USER)
                        .columns(
                                USER.USERNAME, USER.EMAIL, USER.FIRST_NAME,
                                USER.LAST_NAME, USER.COUNTRY, USER.CREATED,
                                USER.UPDATED
                        )
                        .values(
                                record.getUsername(), record.getEmail(), record.getFirstName(),
                                record.getLastName(), record.getCountry(), record.getCreated(),
                                record.getUpdated()
                        )
                        .returning(USER.ID)
                        .fetchOne(USER.ID);
                record.setId(id);
            } else {
                dsl.executeUpdate(record);
            }
        } catch (DataAccessException e) {
            String errorMessage = isNew
                    ? CREATE_USER_ERROR.formatted(record)
                    : UPDATE_USER_ERROR.formatted(record.getId());
            throw new TransactionFailedException(errorMessage, e);
        }
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Override
    public Optional<UserRecord> findById(Long id) {
        return Optional.ofNullable(
                dsl.selectFrom(USER)
                        .where(USER.ID.eq(id))
                        .fetchOne()
        );
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Override
    public Optional<UserRecord> findByUsername(@Nullable String username) {
        return Optional.ofNullable(
                dsl.selectFrom(USER)
                        .where(USER.USERNAME.equalIgnoreCase(username))
                        .fetchOne()
        );
    }

}

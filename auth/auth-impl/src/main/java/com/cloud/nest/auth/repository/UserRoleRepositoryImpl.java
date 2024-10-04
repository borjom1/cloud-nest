package com.cloud.nest.auth.repository;

import com.cloud.nest.db.auth.tables.records.UserRoleRecord;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.cloud.nest.db.auth.Tables.USER_ROLE;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Repository
@RequiredArgsConstructor
public class UserRoleRepositoryImpl implements UserRoleRepository {

    private final DSLContext dsl;

    @Transactional(propagation = MANDATORY)
    @Override
    public void insert(@NotNull UserRoleRecord record) {
        dsl.executeInsert(record);
    }

    @Transactional(propagation = MANDATORY, readOnly = true)
    @Override
    public List<UserRoleRecord> getUserRoles(Long userId) {
        return dsl.selectFrom(USER_ROLE)
                .where(USER_ROLE.USER_ID.eq(userId))
                .fetchInto(UserRoleRecord.class);
    }

}

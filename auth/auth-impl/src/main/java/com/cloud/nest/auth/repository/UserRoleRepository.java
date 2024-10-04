package com.cloud.nest.auth.repository;

import com.cloud.nest.db.auth.tables.records.UserRoleRecord;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface UserRoleRepository {
    void insert(@NotNull UserRoleRecord record);

    List<UserRoleRecord> getUserRoles(Long userId);
}

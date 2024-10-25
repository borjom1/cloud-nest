package com.cloud.nest.auth.repository;

import com.cloud.nest.db.auth.tables.records.UserRecord;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public interface UserRepository {
    void insert(@NotNull UserRecord record);

    boolean existsById(long id);

    @Nullable
    UserRecord findByUsername(String username);

    @Nullable
    UserRecord findById(Long id);
}

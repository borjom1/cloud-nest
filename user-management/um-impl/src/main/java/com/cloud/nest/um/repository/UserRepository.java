package com.cloud.nest.um.repository;

import com.cloud.nest.db.um.tables.records.UserRecord;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface UserRepository {
    void save(@NotNull UserRecord record);

    Optional<UserRecord> findById(@Nullable Long id);

    boolean existsByUsername(@Nullable String username);

    boolean existsByEmail(@Nullable String email);
}

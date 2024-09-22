package com.cloud.nest.um.repository;

import com.cloud.nest.db.um.tables.records.UserRecord;
import jakarta.annotation.Nullable;

public interface UserRepository {
    void save(UserRecord record);

    @Nullable
    UserRecord findById(Long id);
}

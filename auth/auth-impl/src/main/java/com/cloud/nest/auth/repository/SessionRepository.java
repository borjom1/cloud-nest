package com.cloud.nest.auth.repository;

import com.cloud.nest.db.auth.tables.records.SessionRecord;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface SessionRepository {
    void insert(@NotNull SessionRecord record);

    void update(@NotNull SessionRecord record);

    @Nullable
    SessionRecord findById(@NotNull String id);

    boolean existsActiveSession(String clientIp);

    int deleteExpiredSessions();

    int deactivateAllSessionsByUserId(Long userId);

    List<SessionRecord> findAllActiveByUserId(Long userId);
}

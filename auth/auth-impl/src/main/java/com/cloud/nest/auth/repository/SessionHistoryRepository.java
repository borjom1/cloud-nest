package com.cloud.nest.auth.repository;

import com.cloud.nest.db.auth.tables.records.SessionHistoryRecord;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionHistoryRepository {
    void insert(@NotNull SessionHistoryRecord record);

    void update(@NotNull SessionHistoryRecord record);

    List<SessionHistoryRecord> findAllByUserIdOrderedByCreated(Long userId, int offset, int limit);

    void updateLastActiveBySessionIdAndUserId(String sessionId, Long userId, LocalDateTime now);
}

package com.cloud.nest.auth.repository;

import com.cloud.nest.db.auth.tables.records.SessionHistoryRecord;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface SessionHistoryRepository {
    void save(@NotNull SessionHistoryRecord record);

    List<SessionHistoryRecord> findAllByUserIdOrderedByCreated(Long userId, int offset, int limit);
}

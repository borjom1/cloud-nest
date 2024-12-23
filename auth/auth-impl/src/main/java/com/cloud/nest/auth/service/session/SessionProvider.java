package com.cloud.nest.auth.service.session;

import com.cloud.nest.auth.inout.response.ActiveSessionOut;
import com.cloud.nest.auth.inout.response.SessionHistoryOut;
import com.cloud.nest.db.auth.tables.records.SessionRecord;
import com.cloud.nest.platform.model.auth.UserRole;
import jakarta.annotation.Nullable;

import java.util.List;

public interface SessionProvider {

    @Nullable
    SessionRecord findById(String sessionId);

    List<ActiveSessionOut> getActiveSessionsByUserId(Long userId);

    List<SessionHistoryOut> getSessionHistoryByUserId(Long userId, int offset, int limit);

    boolean isSessionActive(@Nullable SessionRecord record);

    List<UserRole> extractRoles(SessionRecord record);
}

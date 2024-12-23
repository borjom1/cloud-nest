package com.cloud.nest.auth.service.session;

import com.cloud.nest.auth.model.RefreshToken;
import com.cloud.nest.auth.model.SessionProperties;
import com.cloud.nest.auth.model.TokenSession;
import com.cloud.nest.db.auth.tables.records.SessionRecord;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.cloud.nest.platform.model.request.ClientRequestDetails;

public interface SessionManager extends SessionProvider {
    TokenSession createSession(SessionProperties properties);

    TokenSession refreshSession(RefreshToken refreshToken, ClientRequestDetails requestDetails);

    void logout(UserAuthSession session);

    void updateLastActive(SessionRecord record);

    void deactivateAllSessionsByUserId(Long userId);
}

package com.cloud.nest.auth.service;

import com.cloud.nest.auth.exception.AuthError;
import com.cloud.nest.auth.exception.AuthException;
import com.cloud.nest.auth.inout.SessionOut;
import com.cloud.nest.auth.inout.SessionStatus;
import com.cloud.nest.auth.jwt.TokenVerifier;
import com.cloud.nest.auth.mapper.AuthMapper;
import com.cloud.nest.auth.model.Token;
import com.cloud.nest.platform.infrastructure.auth.BearerUtils;
import com.cloud.nest.db.auth.tables.records.SessionRecord;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final TokenVerifier tokenVerifier;
    private final SessionService sessionService;
    private final AuthMapper authMapper;

    @Transactional
    @NotNull
    public SessionOut authorize(String bearerToken) {
        final String accessToken = BearerUtils.removeBearerPrefix(bearerToken);
        final Token token = tokenVerifier.verifyAccessToken(accessToken);
        final SessionRecord sessionRecord = sessionService.findById(token.subject());

        if (sessionRecord == null) {
            throw new AuthException(AuthError.SESSION_NOT_FOUND);
        }

        if (SessionStatus.valueOf(sessionRecord.getStatus()) == SessionStatus.DISABLED) {
            throw new AuthException(AuthError.SESSION_DISABLED);
        }

        return authMapper.toOut(sessionRecord);
    }

}

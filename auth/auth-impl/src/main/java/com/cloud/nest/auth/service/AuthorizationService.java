package com.cloud.nest.auth.service;

import com.cloud.nest.auth.exception.AuthError;
import com.cloud.nest.auth.exception.AuthException;
import com.cloud.nest.auth.inout.AccessTokenOut;
import com.cloud.nest.auth.inout.SessionOut;
import com.cloud.nest.auth.jwt.TokenVerifier;
import com.cloud.nest.auth.mapper.AuthMapper;
import com.cloud.nest.auth.model.AccessToken;
import com.cloud.nest.auth.model.RefreshToken;
import com.cloud.nest.auth.model.TokenSession;
import com.cloud.nest.auth.utils.CookieUtils;
import com.cloud.nest.db.auth.tables.records.SessionRecord;
import com.cloud.nest.platform.infrastructure.auth.BearerUtils;
import com.cloud.nest.platform.infrastructure.response.ComplexResponse;
import com.cloud.nest.platform.model.request.ClientRequestDetails;
import jakarta.servlet.http.Cookie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.cloud.nest.auth.impl.AuthApiExternalStandalone.BASE_URL;
import static com.cloud.nest.auth.impl.AuthApiExternalStandalone.URL_REFRESH;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final TokenVerifier tokenVerifier;
    private final SessionService sessionService;
    private final AuthMapper authMapper;

    @Transactional
    @NotNull
    public SessionOut authorize(@NotBlank String bearerToken, @NotNull ClientRequestDetails requestDetails) {
        final String accessToken = BearerUtils.removeBearerPrefix(bearerToken);
        final AccessToken token = tokenVerifier.verifyAccessToken(accessToken);
        final SessionRecord sessionRecord = sessionService.findById(token.getSubject());

        if (sessionRecord == null) {
            throw new AuthException(AuthError.SESSION_NOT_FOUND);
        }

        if (!sessionRecord.getClientIp().equals(requestDetails.clientIp())) {
            throw new AuthException(AuthError.SESSION_MISMATCH);
        }

        if (!sessionService.isSessionActive(sessionRecord)) {
            throw new AuthException(AuthError.SESSION_NOT_ACTIVE);
        }

        return authMapper.toOut(sessionRecord);
    }

    @Transactional
    @NotNull
    public ComplexResponse<AccessTokenOut> refreshUserSession(
            @NotNull Cookie refreshCookie,
            @NotNull ClientRequestDetails clientRequestDetails
    ) {
        final RefreshToken token;
        try {
            token = tokenVerifier.verifyRefreshToken(refreshCookie.getValue());
        } catch (IllegalArgumentException e) {
            throw new AuthException(AuthError.INVALID_CREDENTIALS);
        }

        final TokenSession tokenSession = sessionService.refreshSession(token, clientRequestDetails);
        final ResponseCookie newRefreshCookie = CookieUtils.createRefreshCookie(
                tokenSession.refreshToken(),
                BASE_URL + URL_REFRESH,
                tokenSession.refreshTokenTtl()
        );

        return ComplexResponse.of(
                AccessTokenOut.builder()
                        .access(tokenSession.accessToken())
                        .ttlInSeconds(tokenSession.accessTokenTtl().getSeconds())
                        .build(),
                newRefreshCookie
        );
    }

}

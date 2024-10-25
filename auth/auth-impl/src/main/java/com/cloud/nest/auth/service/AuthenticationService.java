package com.cloud.nest.auth.service;

import com.cloud.nest.auth.exception.AuthError;
import com.cloud.nest.auth.exception.AuthException;
import com.cloud.nest.auth.inout.AccessTokenOut;
import com.cloud.nest.auth.inout.UserAuthIn;
import com.cloud.nest.auth.model.SessionProperties;
import com.cloud.nest.auth.model.TokenSession;
import com.cloud.nest.auth.utils.CookieUtils;
import com.cloud.nest.db.auth.tables.records.UserRecord;
import com.cloud.nest.platform.infrastructure.response.ComplexResponse;
import com.cloud.nest.platform.model.request.ClientRequestDetails;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.cloud.nest.auth.impl.AuthApiExternalStandalone.BASE_URL;
import static com.cloud.nest.auth.impl.AuthApiExternalStandalone.URL_REFRESH;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ComplexResponse<AccessTokenOut> authenticateUser(
            @NotNull UserAuthIn in,
            @NotNull ClientRequestDetails requestDetails
    ) {
        if (sessionService.sessionExistsForIpAddress(requestDetails.clientIp())) {
            throw new AuthException(AuthError.SESSION_EXISTS);
        }

        final Optional<UserRecord> optUserRecord = userService.getByUsername(in.username());
        if (optUserRecord.isEmpty()) {
            throw new AuthException(AuthError.INVALID_CREDENTIALS);
        }

        if (!passwordEncoder.matches(in.password(), optUserRecord.get().getPassword())) {
            throw new AuthException(AuthError.INVALID_CREDENTIALS);
        }

        final TokenSession tokenSession = sessionService.createSession(
                SessionProperties.builder()
                        .userId(optUserRecord.get().getId())
                        .username(in.username())
                        .requestDetails(requestDetails)
                        .build()
        );

        final ResponseCookie refreshCookie = CookieUtils.createRefreshCookie(
                tokenSession.refreshToken(),
                BASE_URL + URL_REFRESH,
                tokenSession.refreshTokenTtl()
        );

        return ComplexResponse.of(
                AccessTokenOut.builder()
                        .access(tokenSession.accessToken())
                        .ttlInSeconds(tokenSession.accessTokenTtl().getSeconds())
                        .build(),
                refreshCookie
        );
    }

}

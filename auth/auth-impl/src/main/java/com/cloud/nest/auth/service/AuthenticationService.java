package com.cloud.nest.auth.service;

import com.cloud.nest.auth.exception.AuthError;
import com.cloud.nest.auth.exception.AuthException;
import com.cloud.nest.auth.impl.AuthApiExternalStandalone;
import com.cloud.nest.auth.inout.UserAuthIn;
import com.cloud.nest.auth.inout.AccessTokenOut;
import com.cloud.nest.auth.model.SessionProperties;
import com.cloud.nest.auth.model.TokenSession;
import com.cloud.nest.auth.utils.CookieUtils;
import com.cloud.nest.platform.infrastructure.response.ComplexResponse;
import com.cloud.nest.platform.model.request.ClientRequestDetails;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CompletableFuture<ComplexResponse<AccessTokenOut>> authenticateUser(
            @NotNull UserAuthIn in,
            @NotNull ClientRequestDetails requestDetails
    ) {
        if (sessionService.sessionExistsForIpAddress(requestDetails.clientIp())) {
            throw new AuthException(AuthError.SESSION_EXISTS);
        }

        userService.getUserByUsername(in.username()).ifPresentOrElse(
                foundUserRecord -> {
                    if (!passwordEncoder.matches(in.password(), foundUserRecord.getPassword())) {
                        throw new AuthException(AuthError.INVALID_CREDENTIALS);
                    }
                },
                () -> {
                    throw new AuthException(AuthError.INVALID_CREDENTIALS);
                }
        );

        final TokenSession tokenSession = sessionService.createSession(
                SessionProperties.builder()
                        .username(in.username())
                        .requestDetails(requestDetails)
                        .build()
        );

        final ResponseCookie refreshCookie = CookieUtils.createRefreshCookie(
                tokenSession.refreshToken(),
                AuthApiExternalStandalone.BASE_URL,
                tokenSession.refreshTokenTtl()
        );

        return completedFuture(
                ComplexResponse.of(
                        AccessTokenOut.builder()
                                .access(tokenSession.accessToken())
                                .ttlInSeconds(tokenSession.accessTokenTtl().getSeconds())
                                .build(),
                        refreshCookie
                )
        );
    }

}

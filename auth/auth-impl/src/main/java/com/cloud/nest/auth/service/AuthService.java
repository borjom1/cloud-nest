package com.cloud.nest.auth.service;

import com.cloud.nest.auth.exception.AuthError;
import com.cloud.nest.auth.exception.AuthException;
import com.cloud.nest.auth.inout.request.ChangePasswordIn;
import com.cloud.nest.auth.inout.request.UserAuthIn;
import com.cloud.nest.auth.inout.response.AccessTokenOut;
import com.cloud.nest.auth.jwt.TokenVerifier;
import com.cloud.nest.auth.model.AccessToken;
import com.cloud.nest.auth.model.RefreshToken;
import com.cloud.nest.auth.model.SessionProperties;
import com.cloud.nest.auth.model.TokenSession;
import com.cloud.nest.auth.service.session.SessionManager;
import com.cloud.nest.auth.utils.CookieUtils;
import com.cloud.nest.db.auth.tables.records.SessionRecord;
import com.cloud.nest.db.auth.tables.records.UserRecord;
import com.cloud.nest.platform.infrastructure.auth.BearerUtils;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.cloud.nest.platform.infrastructure.response.ComplexResponse;
import com.cloud.nest.platform.model.exception.UnexpectedException;
import com.cloud.nest.platform.model.request.ClientRequestDetails;
import jakarta.servlet.http.Cookie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.cloud.nest.auth.AuthApiExternal.URL_REFRESH_COOKIE;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final TokenVerifier tokenVerifier;
    private final SessionManager sessionManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ComplexResponse<AccessTokenOut> authenticateUser(
            @NotNull UserAuthIn in,
            @NotNull ClientRequestDetails requestDetails
    ) {
        final Optional<UserRecord> optUserRecord = userService.getByUsername(in.username());
        if (optUserRecord.isEmpty() ||
            !passwordEncoder.matches(in.password(), optUserRecord.get().getPassword())) {
            throw new AuthException(AuthError.INVALID_CREDENTIALS);
        }

        final TokenSession tokenSession = sessionManager.createSession(
                SessionProperties.builder()
                        .userId(optUserRecord.get().getId())
                        .username(in.username())
                        .requestDetails(requestDetails)
                        .build()
        );

        final ResponseCookie refreshCookie = CookieUtils.createRefreshCookie(
                tokenSession.refreshToken(),
                URL_REFRESH_COOKIE,
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

    @Transactional
    @NotNull
    public UserAuthSession authorize(@NotBlank String bearerToken, @NotNull ClientRequestDetails requestDetails) {
        final String accessToken = BearerUtils.removeBearerPrefix(bearerToken);
        final AccessToken token = tokenVerifier.verifyAccessToken(accessToken);
        final SessionRecord record = sessionManager.findById(token.getSubject());

        if (record == null) {
            throw new AuthException(AuthError.SESSION_NOT_FOUND);
        }

        if (!record.getClientIp().equals(requestDetails.clientIp())) {
            throw new AuthException(AuthError.SESSION_MISMATCH);
        }

        if (!sessionManager.isSessionActive(record)) {
            throw new AuthException(AuthError.SESSION_NOT_ACTIVE);
        }

        sessionManager.updateLastActive(record);

        return UserAuthSession.builder()
                .sessionId(record.getId())
                .userId(record.getUserId())
                .username(record.getUsername())
                .roles(sessionManager.extractRoles(record))
                .build();
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

        final TokenSession tokenSession = sessionManager.refreshSession(token, clientRequestDetails);
        final ResponseCookie newRefreshCookie = CookieUtils.createRefreshCookie(
                tokenSession.refreshToken(),
                URL_REFRESH_COOKIE,
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

    @Transactional
    public void changePassword(@NotNull UserAuthSession session, @NotNull ChangePasswordIn in) {
        if (in.oldPassword().equals(in.newPassword())) {
            throw new IllegalArgumentException("New password must differ from an old password");
        }

        final UserRecord record = userService.getById(session.userId()).orElseThrow(
                () -> new UnexpectedException("Not found user with id [%d]".formatted(session.userId()))
        );

        if (!passwordEncoder.matches(in.oldPassword(), record.getPassword())) {
            throw new AuthException(AuthError.INVALID_CREDENTIALS);
        }

        record.setPassword(passwordEncoder.encode(in.newPassword()));
        userService.update(record);
        sessionManager.deactivateAllSessionsByUserId(session.userId());
    }

    @Transactional
    public void logout(@NotNull UserAuthSession session) {
        sessionManager.logout(session);
    }

}

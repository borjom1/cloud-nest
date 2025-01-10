package com.cloud.nest.auth.controller;

import com.cloud.nest.auth.AuthApiExternal;
import com.cloud.nest.auth.inout.request.ChangePasswordIn;
import com.cloud.nest.auth.inout.request.UserAuthIn;
import com.cloud.nest.auth.inout.response.AccessTokenOut;
import com.cloud.nest.auth.inout.response.ActiveSessionOut;
import com.cloud.nest.auth.inout.response.SessionHistoryOut;
import com.cloud.nest.auth.service.AuthService;
import com.cloud.nest.auth.service.session.SessionProvider;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.cloud.nest.platform.infrastructure.request.RequestUtils;
import com.cloud.nest.platform.infrastructure.response.ComplexResponse;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.auth.AuthApiExternal.BASE_URL;
import static com.cloud.nest.auth.utils.CookieUtils.AUTH_REFRESH_COOKIE;
import static com.cloud.nest.platform.infrastructure.request.RequestUtils.USER_SESSION_HEADER;
import static com.cloud.nest.platform.infrastructure.request.RequestUtils.getRequestClientDetails;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class AuthApiExternalController implements AuthApiExternal {

    private final AuthService authService;
    private final SessionProvider sessionProvider;

    @PostMapping(URL_LOGIN)
    @ResponseStatus(CREATED)
    @Override
    public CompletableFuture<ComplexResponse<AccessTokenOut>> authenticateUser(@Valid @RequestBody UserAuthIn in) {
        return completedFuture(authService.authenticate(in, getRequestClientDetails()));
    }

    @PostMapping(URL_REFRESH)
    @ResponseStatus(CREATED)
    @Override
    public CompletableFuture<ComplexResponse<AccessTokenOut>> refreshSession(
            @CookieValue(AUTH_REFRESH_COOKIE) Cookie refreshCookie
    ) {
        return completedFuture(
                authService.refreshUserSession(refreshCookie, RequestUtils.getRequestClientDetails())
        );
    }

    @DeleteMapping(URL_USER + URL_SESSIONS)
    @ResponseStatus(NO_CONTENT)
    @Override
    public CompletableFuture<Void> logout(@RequestHeader(USER_SESSION_HEADER) UserAuthSession session) {
        authService.logout(session);
        return completedFuture(null);
    }

    @PatchMapping(URL_USER + URL_PASSWORD)
    @Override
    public CompletableFuture<Void> changePassword(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @Valid @RequestBody ChangePasswordIn in
    ) {
        authService.changePassword(session, in);
        return completedFuture(null);
    }

    @GetMapping(URL_USER + URL_SESSIONS)
    @Override
    public CompletableFuture<List<ActiveSessionOut>> getActiveSessions(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session
    ) {
        return completedFuture(sessionProvider.getActiveSessionsByUserId(session.userId()));
    }

    @GetMapping(URL_USER + URL_HISTORY)
    @Override
    public CompletableFuture<List<SessionHistoryOut>> getSessionHistory(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @RequestParam(PARAM_OFFSET) int offset,
            @RequestParam(PARAM_LIMIT) int limit
    ) {
        return completedFuture(sessionProvider.getSessionHistoryByUserId(session.userId(), offset, limit));
    }

}

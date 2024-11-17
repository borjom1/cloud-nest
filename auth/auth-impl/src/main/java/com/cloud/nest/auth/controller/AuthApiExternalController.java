package com.cloud.nest.auth.controller;

import com.cloud.nest.auth.AuthApiExternal;
import com.cloud.nest.auth.inout.response.AccessTokenOut;
import com.cloud.nest.auth.inout.request.UserAuthIn;
import com.cloud.nest.auth.inout.response.SessionHistoryOut;
import com.cloud.nest.auth.service.AuthenticationService;
import com.cloud.nest.auth.service.AuthorizationService;
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

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class AuthApiExternalController implements AuthApiExternal {

    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;

    @PostMapping(URL_LOGIN)
    @ResponseStatus(CREATED)
    @Override
    public CompletableFuture<ComplexResponse<AccessTokenOut>> authenticateUser(@Valid @RequestBody UserAuthIn in) {
        return completedFuture(authenticationService.authenticateUser(in, getRequestClientDetails()));
    }

    @PostMapping(URL_REFRESH)
    @ResponseStatus(CREATED)
    @Override
    public CompletableFuture<ComplexResponse<AccessTokenOut>> refreshSession(
            @CookieValue(AUTH_REFRESH_COOKIE) Cookie refreshCookie
    ) {
        return completedFuture(
                authorizationService.refreshUserSession(refreshCookie, RequestUtils.getRequestClientDetails())
        );
    }

    @GetMapping(URL_USER + URL_HISTORY)
    @Override
    public CompletableFuture<List<SessionHistoryOut>> getSessionHistory(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @RequestParam(PARAM_OFFSET) int offset,
            @RequestParam(PARAM_LIMIT) int limit
    ) {
        return completedFuture(authenticationService.getAuthSessionHistory(session, offset, limit));
    }

}

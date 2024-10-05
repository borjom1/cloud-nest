package com.cloud.nest.auth.controller;

import com.cloud.nest.auth.AuthApiExternal;
import com.cloud.nest.auth.inout.AccessTokenOut;
import com.cloud.nest.auth.inout.UserAuthIn;
import com.cloud.nest.auth.service.AuthenticationService;
import com.cloud.nest.auth.service.AuthorizationService;
import com.cloud.nest.platform.infrastructure.request.RequestUtils;
import com.cloud.nest.platform.infrastructure.response.ComplexResponse;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.auth.impl.AuthApiExternalStandalone.BASE_URL;
import static com.cloud.nest.auth.impl.AuthApiExternalStandalone.URL_REFRESH;
import static com.cloud.nest.auth.utils.CookieUtils.AUTH_REFRESH_COOKIE;
import static com.cloud.nest.platform.infrastructure.request.RequestUtils.getRequestClientDetails;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class AuthApiExternalController implements AuthApiExternal {

    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;

    @ResponseStatus(CREATED)
    @PostMapping
    @Override
    public CompletableFuture<ComplexResponse<AccessTokenOut>> authenticateUser(@Valid @RequestBody UserAuthIn in) {
        return completedFuture(authenticationService.authenticateUser(in, getRequestClientDetails()));
    }

    @ResponseStatus(CREATED)
    @PostMapping(URL_REFRESH)
    @Override
    public CompletableFuture<ComplexResponse<AccessTokenOut>> refreshSession(
            @CookieValue(AUTH_REFRESH_COOKIE) Cookie refreshCookie
    ) {
        return completedFuture(
                authorizationService.refreshUserSession(refreshCookie, RequestUtils.getRequestClientDetails())
        );
    }

}

package com.cloud.nest.auth.controller;

import com.cloud.nest.auth.AuthApiInternal;
import com.cloud.nest.auth.inout.request.NewAuthUserIn;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.cloud.nest.auth.service.AuthorizationService;
import com.cloud.nest.auth.service.UserService;
import com.cloud.nest.platform.infrastructure.request.RequestUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.auth.impl.AuthApiInternalStandalone.BASE_URL;
import static com.cloud.nest.auth.impl.AuthApiInternalStandalone.URL_USERS;
import static com.cloud.nest.platform.infrastructure.request.RequestUtils.X_FORWARDED_FOR;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class AuthApiInternalController implements AuthApiInternal {

    private final AuthorizationService authorizationService;
    private final UserService userService;

    @PostMapping(URL_USERS)
    @Override
    public CompletableFuture<Void> createUser(@Valid @RequestBody NewAuthUserIn in) {
        userService.saveUser(in);
        return completedFuture(null);
    }

    @PostMapping
    @Override
    public CompletableFuture<UserAuthSession> authorize(
            @RequestHeader(AUTHORIZATION) String bearerToken,
            @RequestHeader(value = X_FORWARDED_FOR, required = false) String clientIp
    ) {
        return completedFuture(authorizationService.authorize(bearerToken, RequestUtils.getRequestClientDetails()));
    }

}

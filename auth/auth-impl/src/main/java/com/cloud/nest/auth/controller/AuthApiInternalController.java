package com.cloud.nest.auth.controller;

import com.cloud.nest.auth.AuthApiInternal;
import com.cloud.nest.auth.inout.NewAuthUserIn;
import com.cloud.nest.auth.inout.SessionOut;
import com.cloud.nest.auth.service.AuthorizationService;
import com.cloud.nest.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.auth.impl.AuthApiInternalStandalone.BASE_URL;
import static com.cloud.nest.auth.impl.AuthApiInternalStandalone.URL_USERS;
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
    public CompletableFuture<SessionOut> authorize(@RequestHeader(AUTHORIZATION) String bearerToken) {
        return completedFuture(authorizationService.authorize(bearerToken));
    }

}

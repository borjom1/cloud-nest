package com.cloud.nest.auth.controller;

import com.cloud.nest.auth.AuthApiExternal;
import com.cloud.nest.auth.inout.UserAuthIn;
import com.cloud.nest.auth.inout.AccessTokenOut;
import com.cloud.nest.auth.service.AuthenticationService;
import com.cloud.nest.platform.infrastructure.response.ComplexResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.auth.impl.AuthApiExternalStandalone.BASE_URL;
import static com.cloud.nest.platform.infrastructure.request.RequestUtils.getRequestClientDetails;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class AuthApiExternalController implements AuthApiExternal {

    private final AuthenticationService authenticationService;

    @PostMapping
    @Override
    public CompletableFuture<ComplexResponse<AccessTokenOut>> authenticateUser(@Valid @RequestBody UserAuthIn in) {
        return authenticationService.authenticateUser(in, getRequestClientDetails());
    }

}

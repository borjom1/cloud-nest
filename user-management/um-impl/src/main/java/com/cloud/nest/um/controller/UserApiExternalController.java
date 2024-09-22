package com.cloud.nest.um.controller;

import com.cloud.nest.um.UserApiExternal;
import com.cloud.nest.um.inout.UserIn;
import com.cloud.nest.um.inout.UserOut;
import com.cloud.nest.um.service.UserApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.um.impl.UserApiExternalStandalone.*;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(URL_USERS)
@RequiredArgsConstructor
public class UserApiExternalController implements UserApiExternal {

    private final UserApiService userApiService;

    @PostMapping
    @ResponseStatus(CREATED)
    @Override
    public CompletableFuture<UserOut> createUser(@Valid @RequestBody UserIn in) {
        return completedFuture(userApiService.createUser(in));
    }

    @GetMapping(PATH_ID)
    @Override
    public CompletableFuture<UserOut> findById(@PathVariable(PARAM_ID) Long id) {
        return completedFuture(userApiService.findById(id));
    }

}

package com.cloud.nest.um.controller;

import com.cloud.nest.um.UserApiInternal;
import com.cloud.nest.um.inout.UserUpdateIn;
import com.cloud.nest.um.service.UserApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.um.impl.UserApiInternalStandalone.*;
import static java.util.concurrent.CompletableFuture.completedFuture;

@RestController
@RequestMapping(URL_USERS)
@RequiredArgsConstructor
public class UserApiInternalController implements UserApiInternal {

    private final UserApiService userApiService;

    @PatchMapping(PATH_ID)
    @Override
    public CompletableFuture<Void> updateUser(@PathVariable(PARAM_ID) Long id, @Valid @RequestBody UserUpdateIn in) {
        userApiService.updateUser(id, in);
        return completedFuture(null);
    }

}

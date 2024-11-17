package com.cloud.nest.auth;

import com.cloud.nest.auth.inout.request.NewAuthUserIn;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;

import java.util.concurrent.CompletableFuture;

public interface AuthApiInternal {
    CompletableFuture<Void> createUser(NewAuthUserIn in);

    CompletableFuture<UserAuthSession> authorize(String accessToken, String clientIp);
}

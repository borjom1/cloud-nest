package com.cloud.nest.auth;

import com.cloud.nest.auth.inout.NewAuthUserIn;
import com.cloud.nest.auth.inout.SessionOut;

import java.util.concurrent.CompletableFuture;

public interface AuthApiInternal {
    CompletableFuture<Void> createUser(NewAuthUserIn in);

    CompletableFuture<SessionOut> authorize(String accessToken);
}

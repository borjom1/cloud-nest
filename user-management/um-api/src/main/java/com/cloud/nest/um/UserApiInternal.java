package com.cloud.nest.um;

import com.cloud.nest.um.inout.UserUpdateIn;

import java.util.concurrent.CompletableFuture;

public interface UserApiInternal {
    CompletableFuture<Void> updateUser(Long id, UserUpdateIn in);
}

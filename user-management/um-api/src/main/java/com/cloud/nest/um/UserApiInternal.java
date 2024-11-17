package com.cloud.nest.um;

import com.cloud.nest.um.inout.UserOut;
import com.cloud.nest.um.inout.UserUpdateIn;

import java.util.concurrent.CompletableFuture;

public interface UserApiInternal {
    CompletableFuture<UserOut> findById(Long id);

    CompletableFuture<Void> updateUser(Long id, UserUpdateIn in);
}

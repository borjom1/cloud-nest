package com.cloud.nest.um;

import com.cloud.nest.um.inout.UserIn;
import com.cloud.nest.um.inout.UserOut;

import java.util.concurrent.CompletableFuture;

public interface UserApiExternal {
    CompletableFuture<UserOut> createUser(UserIn in);

    CompletableFuture<UserOut> findById(Long id);
}

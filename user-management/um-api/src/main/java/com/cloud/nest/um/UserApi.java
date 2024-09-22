package com.cloud.nest.um;

import java.util.concurrent.CompletableFuture;

public interface UserApi {
    CompletableFuture<String> getUserString();
}

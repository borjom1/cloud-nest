package com.cloud.nest.auth;

import com.cloud.nest.auth.inout.AccessTokenOut;
import com.cloud.nest.auth.inout.UserAuthIn;
import com.cloud.nest.platform.infrastructure.response.ComplexResponse;
import jakarta.servlet.http.Cookie;

import java.util.concurrent.CompletableFuture;

public interface AuthApiExternal {
    CompletableFuture<ComplexResponse<AccessTokenOut>> authenticateUser(UserAuthIn in);

    CompletableFuture<ComplexResponse<AccessTokenOut>> refreshSession(Cookie refreshCookie);
}

package com.cloud.nest.auth;

import com.cloud.nest.auth.inout.request.UserAuthIn;
import com.cloud.nest.auth.inout.response.AccessTokenOut;
import com.cloud.nest.auth.inout.response.SessionHistoryOut;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.cloud.nest.platform.infrastructure.response.ComplexResponse;
import jakarta.servlet.http.Cookie;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AuthApiExternal {

    String URL_SECURITY = "/v1/security";
    String BASE_URL = "/external" + URL_SECURITY;

    String URL_LOGIN = "/login";
    String URL_REFRESH = "/refresh";
    String URL_REFRESH_COOKIE = "/auth" + URL_SECURITY + URL_REFRESH;

    String URL_USER = "/user";
    String URL_HISTORY = "/history";

    String PARAM_OFFSET = "offset";
    String PARAM_LIMIT = "limit";

    CompletableFuture<ComplexResponse<AccessTokenOut>> authenticateUser(UserAuthIn in);

    CompletableFuture<ComplexResponse<AccessTokenOut>> refreshSession(Cookie refreshCookie);

    CompletableFuture<List<SessionHistoryOut>> getSessionHistory(UserAuthSession session, int offset, int limit);
}

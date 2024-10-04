package com.cloud.nest.auth.utils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

@UtilityClass
public class CookieUtils {

    public static final String AUTH_REFRESH_COOKIE = "Refresh-Cookie";
    public static final String STRICT_SAME_SITE = "Strict";

    @NotNull
    public static ResponseCookie createRefreshCookie(
            @NotBlank String token,
            @NotBlank String path,
            @NotNull Duration maxAge
    ) {
        return ResponseCookie.from(AUTH_REFRESH_COOKIE)
                .value(token)
                .maxAge(maxAge)
                .sameSite(STRICT_SAME_SITE)
                .httpOnly(true)
                .secure(true)
                .path(path)
                .build();
    }

}

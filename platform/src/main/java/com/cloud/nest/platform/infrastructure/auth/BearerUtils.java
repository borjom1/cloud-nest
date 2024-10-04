package com.cloud.nest.platform.infrastructure.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BearerUtils {

    public static final String BEARER_PREFIX = "Bearer ";

    @NotNull
    public static String removeBearerPrefix(@NotBlank String bearerToken) {
        if (!bearerToken.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Invalid bearer token");
        }
        return bearerToken.substring(BEARER_PREFIX.length());
    }

    @NotBlank
    public static String addBearerPrefix(@NotBlank String token) {
        return BEARER_PREFIX + token;
    }

}

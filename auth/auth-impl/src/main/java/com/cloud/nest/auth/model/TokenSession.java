package com.cloud.nest.auth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Duration;

@Builder
public record TokenSession(

        @NotBlank
        String accessToken,

        @NotBlank
        String refreshToken,

        @NotNull
        Duration accessTokenTtl,

        @NotNull
        Duration refreshTokenTtl

) {
}

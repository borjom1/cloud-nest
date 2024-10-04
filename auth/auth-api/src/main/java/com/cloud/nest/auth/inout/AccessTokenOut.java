package com.cloud.nest.auth.inout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
public record AccessTokenOut(@NotBlank String access, @Positive long ttlInSeconds) {
}

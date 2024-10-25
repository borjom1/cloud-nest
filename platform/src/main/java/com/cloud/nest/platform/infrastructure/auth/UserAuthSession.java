package com.cloud.nest.platform.infrastructure.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
public record UserAuthSession(

        @NotNull
        @Positive
        Long userId,

        @NotBlank
        String sessionId,

        @NotBlank
        String username

) {

}

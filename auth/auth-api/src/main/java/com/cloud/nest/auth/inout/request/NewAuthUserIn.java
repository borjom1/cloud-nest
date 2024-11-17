package com.cloud.nest.auth.inout.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
public record NewAuthUserIn(

        @NotNull
        @Positive
        Long userId,

        @NotBlank
        String username,

        @NotBlank
        String password

) {
}

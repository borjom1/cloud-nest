package com.cloud.nest.platform.infrastructure.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
public record UserAuthSession(

        @NotBlank String id,

        @NotBlank
        String username

) {

}

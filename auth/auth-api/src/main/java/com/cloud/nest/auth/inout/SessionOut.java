package com.cloud.nest.auth.inout;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
public record SessionOut(

        @NotBlank String id,

        @NotBlank
        String username

) {

}

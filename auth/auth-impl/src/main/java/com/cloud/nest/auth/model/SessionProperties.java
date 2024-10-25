package com.cloud.nest.auth.model;

import com.cloud.nest.platform.model.request.ClientRequestDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record SessionProperties(

        @NotNull
        @Positive
        Long userId,

        @NotBlank
        String username,

        @NotNull
        ClientRequestDetails requestDetails

) {
}

package com.cloud.nest.auth.model;

import com.cloud.nest.platform.model.request.ClientRequestDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SessionProperties(

        @NotBlank
        String username,

        @NotNull
        ClientRequestDetails requestDetails

) {
}

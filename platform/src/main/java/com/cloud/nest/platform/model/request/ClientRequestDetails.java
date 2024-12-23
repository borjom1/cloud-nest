package com.cloud.nest.platform.model.request;

import jakarta.validation.constraints.NotBlank;

public record ClientRequestDetails(@NotBlank String clientIp, @NotBlank String userAgent) {
}

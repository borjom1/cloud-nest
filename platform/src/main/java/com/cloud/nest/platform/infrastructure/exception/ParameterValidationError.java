package com.cloud.nest.platform.infrastructure.exception;

import lombok.Builder;

import java.util.List;

@Builder
public record ParameterValidationError(String parameterName, List<String> errors) {
}

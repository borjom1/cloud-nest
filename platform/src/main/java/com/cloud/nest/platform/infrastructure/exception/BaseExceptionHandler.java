package com.cloud.nest.platform.infrastructure.exception;

import com.cloud.nest.platform.model.ApiError;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.Instant;

public class BaseExceptionHandler {

    @ExceptionHandler(Exception.class)
    @NotNull
    public ApiError handleGlobalError(@NotNull ServletWebRequest webRequest, @NotNull Exception e) {
        return ApiError.builder()
                .timestamp(Instant.now())
                .error(e.getMessage())
                .uri(webRequest.getRequest().getRequestURI())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .details(null)
                .build();
    }

}

package com.cloud.nest.fm.controller;

import com.cloud.nest.fm.exception.OperationNotAllowedException;
import com.cloud.nest.fm.exception.StorageSpaceLimitExceededException;
import com.cloud.nest.platform.infrastructure.exception.BaseRestExceptionHandler;
import com.cloud.nest.platform.model.ApiError;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestControllerAdvice
public class FmRestExceptionHandler extends BaseRestExceptionHandler {

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(BAD_REQUEST)
    public ApiError handleMissingRequestPartError(
            @NotNull ServletWebRequest webRequest,
            @NotNull MissingServletRequestPartException e
    ) {
        return createDefaultError(e.getMessage(), webRequest, BAD_REQUEST);
    }

    @ExceptionHandler({StorageSpaceLimitExceededException.class, OperationNotAllowedException.class})
    @ResponseStatus(FORBIDDEN)
    public ApiError handleAuthorizationError(
            @NotNull ServletWebRequest webRequest,
            @NotNull RuntimeException e
    ) {
        return createDefaultError(e.getMessage(), webRequest, FORBIDDEN);
    }

}

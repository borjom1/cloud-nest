package com.cloud.nest.fm.controller;

import com.cloud.nest.fm.exception.OperationNotAllowedException;
import com.cloud.nest.fm.exception.StorageSpaceLimitExceededException;
import com.cloud.nest.platform.infrastructure.exception.BaseRestExceptionHandler;
import com.cloud.nest.platform.model.ApiError;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Log4j2
@RestControllerAdvice
public class FmRestExceptionHandler extends BaseRestExceptionHandler {

    private static final Set<String> IGNORABLE_IO_ERRORS = Set.of(
            "An established connection was aborted by the software in your host machine",
            "Connection reset by peer"
    );

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
    public ApiError handleAuthorizationError(@NotNull ServletWebRequest webRequest, @NotNull RuntimeException e) {
        return createDefaultError(e.getMessage(), webRequest, FORBIDDEN);
    }

    @ExceptionHandler(IOException.class)
    public void ignoreIOExceptionError(@NotNull ServletWebRequest webRequest, @NotNull IOException e) {
        if (IGNORABLE_IO_ERRORS.contains(e.getMessage())) {
            return;
        }
        log.info("IO error: path=[{}] cause=[{}]", webRequest.getRequest().getRequestURI(), e.getMessage());
    }

}

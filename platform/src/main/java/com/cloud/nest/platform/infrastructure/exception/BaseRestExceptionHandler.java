package com.cloud.nest.platform.infrastructure.exception;

import com.cloud.nest.platform.model.ApiError;
import com.cloud.nest.platform.model.exception.DataNotFoundException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.*;

@Log4j2
public class BaseRestExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @NotNull
    public ApiError handleGlobalError(@NotNull ServletWebRequest webRequest, @NotNull Exception e) {
        log.error("Handle global error: {}", e.getMessage());
        return createDefaultError(e.getMessage(), webRequest, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(BAD_REQUEST)
    @NotNull
    public ApiError handleIllegalArgumentError(@NotNull ServletWebRequest webRequest, @NotNull IllegalArgumentException e) {
        log.error("Handle illegal argument error: {}", e.getMessage());
        return createDefaultError(e.getMessage(), webRequest, BAD_REQUEST);
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @NotNull
    public ApiError handleDataNotFoundError(@NotNull ServletWebRequest webRequest, @NotNull DataNotFoundException e) {
        log.error("Handle data not found error: {}", e.getMessage());
        return createDefaultError(e.getMessage(), webRequest, NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    @NotNull
    public ApiError handleMethodArgumentNotValidError(
            @NotNull ServletWebRequest webRequest,
            @NotNull MethodArgumentNotValidException e
    ) {
        final Map<String, List<String>> fieldErrorDetails = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(objectError -> (FieldError) objectError)
                .filter(fieldError -> Objects.nonNull(fieldError.getDefaultMessage()))
                .collect(toMap(
                        FieldError::getField,
                        fieldError -> {
                            final var errorMessagesList = new LinkedList<String>();
                            errorMessagesList.add(fieldError.getDefaultMessage());
                            return errorMessagesList;
                        },
                        (errorMessagesList, errorMessagesList2) -> {
                            errorMessagesList.addAll(errorMessagesList2);
                            return errorMessagesList;
                        }
                ));

        return createDefaultError(
                "Request body fields validation failed",
                fieldErrorDetails,
                webRequest,
                BAD_REQUEST
        );
    }

    @NotNull
    protected static ApiError createDefaultError(String errorMessage, ServletWebRequest webRequest, HttpStatus status) {
        return createDefaultError(errorMessage, null, webRequest, status);
    }

    @NotNull
    protected static ApiError createDefaultError(
            @Nullable String errorMessage,
            @Nullable Object details,
            @NotNull ServletWebRequest webRequest,
            @NotNull HttpStatus status
    ) {
        return ApiError.builder()
                .timestamp(Instant.now())
                .error(errorMessage)
                .uri(webRequest.getRequest().getRequestURI())
                .status(status.value())
                .details(details)
                .build();
    }

}

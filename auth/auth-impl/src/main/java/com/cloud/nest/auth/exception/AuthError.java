package com.cloud.nest.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum AuthError {
    SESSION_EXISTS("You are already authenticated", HttpStatus.UNAUTHORIZED),
    SESSION_NOT_FOUND("Session not found", HttpStatus.UNAUTHORIZED),
    SESSION_DISABLED("Session is disabled", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("Invalid credentials", HttpStatus.BAD_REQUEST);

    private final String errorMessage;
    private final HttpStatus status;

}

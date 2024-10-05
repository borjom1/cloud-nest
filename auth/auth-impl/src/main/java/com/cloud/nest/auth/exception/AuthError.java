package com.cloud.nest.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum AuthError {
    SESSION_EXISTS("You are already authenticated", HttpStatus.CONFLICT),
    SESSION_NOT_FOUND("Session not found", HttpStatus.UNAUTHORIZED),
    SESSION_NOT_ACTIVE("Session is not active", HttpStatus.GONE),
    SESSION_MISMATCH("Session mismatch detected", HttpStatus.UNAUTHORIZED),
    REFRESH_NOT_AVAILABLE("Refresh token is no longer active", HttpStatus.GONE),
    INVALID_CREDENTIALS("Invalid credentials", HttpStatus.BAD_REQUEST);

    private final String errorMessage;
    private final HttpStatus status;

}

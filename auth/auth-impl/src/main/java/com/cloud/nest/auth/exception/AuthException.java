package com.cloud.nest.auth.exception;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {

    private final HttpStatus status;

    public AuthException(@NotNull AuthError error) {
        super(error.getErrorMessage());
        this.status = error.getStatus();
    }

}

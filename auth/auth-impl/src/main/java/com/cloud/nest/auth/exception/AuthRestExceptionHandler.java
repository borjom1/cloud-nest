package com.cloud.nest.auth.exception;

import com.cloud.nest.platform.infrastructure.exception.BaseRestExceptionHandler;
import com.cloud.nest.platform.model.ApiError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

@RestControllerAdvice
public class AuthRestExceptionHandler extends BaseRestExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiError> handleAuthError(ServletWebRequest request, AuthException e) {
        final ApiError error = BaseRestExceptionHandler.createDefaultError(e.getMessage(), request, e.getStatus());
        return ResponseEntity.status(e.getStatus().value()).body(error);
    }

}

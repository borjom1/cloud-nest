package com.cloud.nest.platform.model.exception;

public class TransactionFailedException extends RuntimeException {
    public TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

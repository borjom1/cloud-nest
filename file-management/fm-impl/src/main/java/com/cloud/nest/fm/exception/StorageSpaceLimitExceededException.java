package com.cloud.nest.fm.exception;

public class StorageSpaceLimitExceededException extends RuntimeException {
    public StorageSpaceLimitExceededException(String message) {
        super(message);
    }
}

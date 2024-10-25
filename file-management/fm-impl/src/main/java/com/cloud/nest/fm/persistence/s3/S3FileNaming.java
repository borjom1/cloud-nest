package com.cloud.nest.fm.persistence.s3;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class S3FileNaming {

    private static final String SEPARATOR = "/";
    private static final String FILES_PREFIX = SEPARATOR + "files";

    @NotBlank
    public String getObjectKey(@NotNull Long userId, @NotNull String ext) {
        final String objectKeyPrefix = FILES_PREFIX + SEPARATOR + userId + SEPARATOR;
        final String newFilename =  ext.isBlank()
                ? UUID.randomUUID().toString()
                : "%s.%s".formatted(UUID.randomUUID(), ext);
        return objectKeyPrefix + newFilename;
    }

}

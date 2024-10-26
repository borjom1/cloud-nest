package com.cloud.nest.fm.util;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

public class MediaTypeMapper {

    @NotNull
    public static MediaType getMediaTypeForFileExtension(@Nullable String extension) {
        return MediaTypeFactory
                .getMediaType("." + extension)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

}

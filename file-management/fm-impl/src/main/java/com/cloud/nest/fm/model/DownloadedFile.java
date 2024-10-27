package com.cloud.nest.fm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@Builder
public record DownloadedFile(

        @NotBlank
        String name,

        @NotNull
        MediaType contentType,

        @Positive
        long size,

        @NotNull
        Resource resource

) {
}

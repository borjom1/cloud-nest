package com.cloud.nest.fm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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

    public ResponseEntity<Resource> toResponseEntity() {
        return ResponseEntity.ok()
                .contentType(contentType)
                .contentLength(size)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(name))
                .body(resource);
    }

}

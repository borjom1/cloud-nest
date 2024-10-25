package com.cloud.nest.fm.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UploadedFile(
        @NotBlank String s3ObjectKey
) {
}

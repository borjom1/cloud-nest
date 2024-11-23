package com.cloud.nest.fm.model;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record FileSearchCriteria(
        @Nullable String filename,
        @Nullable String ext,
        @Nullable Long minFileSize,
        @Nullable Long maxFileSize
) {
}

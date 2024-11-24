package com.cloud.nest.fm.model;

import com.cloud.nest.fm.util.filter.GreaterOrEqual;
import com.cloud.nest.fm.util.filter.LessOrEqual;
import com.cloud.nest.fm.util.filter.Like;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record FileFilter(
        @Nullable @Like String filename,
        @Nullable @Like String ext,
        @Nullable @GreaterOrEqual("size") Long minFileSize,
        @Nullable @LessOrEqual("size") Long maxFileSize
) {
}

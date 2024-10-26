package com.cloud.nest.fm.inout;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Jacksonized
public record UploadedFileOut(

        @NotNull
        Long id,

        @NotNull
        String filename,

        @Nullable
        String ext,

        long size,

        @NotNull
        @Past
        @JsonFormat(shape = STRING)
        LocalDateTime created

) {
}

package com.cloud.nest.fm.inout;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Jacksonized
public record FileOut(

        @NotNull
        Long id,

        @NotBlank
        String filename,

        @Nullable
        String ext,

        @NotNull
        Long size,

        @NotNull
        Boolean deleted,

        @NotNull
        @Past
        @JsonFormat(shape = STRING)
        LocalDateTime created,

        @NotNull
        @Past
        @JsonFormat(shape = STRING)
        LocalDateTime updated

) {
}

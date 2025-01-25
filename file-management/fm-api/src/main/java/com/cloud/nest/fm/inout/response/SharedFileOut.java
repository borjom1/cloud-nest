package com.cloud.nest.fm.inout.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Jacksonized
public record SharedFileOut(

        @NotNull
        UUID shareId,

        @NotNull
        Boolean password,

        @NotNull
        @JsonFormat(shape = STRING)
        LocalDateTime created,

        @Nullable
        @JsonFormat(shape = STRING)
        LocalDateTime expiresAt,

        @NotNull
        Integer downloads

) {
}

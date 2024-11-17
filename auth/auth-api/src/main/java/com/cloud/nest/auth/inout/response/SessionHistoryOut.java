package com.cloud.nest.auth.inout.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Jacksonized
public record SessionHistoryOut(

        @NotNull
        Long id,

        @NotNull
        @Past
        @JsonFormat(shape = STRING)
        LocalDateTime created,

        @NotBlank
        String userAgent,

        @NotBlank
        String clientIp

) {
}

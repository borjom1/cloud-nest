package com.cloud.nest.fm.inout.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Jacksonized
public record UserReportOut(

        @NotNull
        @Past
        @JsonFormat(shape = STRING)
        LocalDate periodStart,

        @NotNull
        @Past
        @JsonFormat(shape = STRING)
        LocalDate periodEnd,

        @NotNull
        Long downloadedBytes,

        @NotNull
        Long uploadedBytes,

        @NotNull
        @Past
        @JsonFormat(shape = STRING)
        LocalDateTime created

) {
}

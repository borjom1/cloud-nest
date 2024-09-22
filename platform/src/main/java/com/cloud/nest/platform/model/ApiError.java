package com.cloud.nest.platform.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Jacksonized
@Builder
@JsonInclude(NON_NULL)
public record ApiError(

        @NotNull
        String uri,

        int status,

        @NotNull
        String error,

        @NotNull
        @Past
        Instant timestamp,

        Object details

) {
}

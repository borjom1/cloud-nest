package com.cloud.nest.platform.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Jacksonized
@Builder
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

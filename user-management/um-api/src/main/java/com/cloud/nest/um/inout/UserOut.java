package com.cloud.nest.um.inout;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Jacksonized
@Builder
public record UserOut(

        @NotNull
        Long id,

        @NotNull
        String username,

        @NotNull
        String email,

        @NotNull
        String firstName,

        @NotNull
        String lastName,

        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDateTime created

) {
}

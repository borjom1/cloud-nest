package com.cloud.nest.um.inout;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Jacksonized
@Builder
public record UserInternalOut(

        @NotBlank
        String password,

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

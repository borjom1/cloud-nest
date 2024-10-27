package com.cloud.nest.fm.inout.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;

import java.time.ZonedDateTime;

import static com.cloud.nest.platform.model.validation.CommonPattern.AT_LEAST_ONE_SPECIAL_CHAR;

@Builder
@Jacksonized
public record SharedFileIn(

        @Nullable
        @Pattern(
                regexp = AT_LEAST_ONE_SPECIAL_CHAR,
                message = "must contain at least one special character"
        )
        @Length(min = 6, max = 24)
        String password,

        @Nullable
        @Future
        ZonedDateTime expiresAt

) {
}

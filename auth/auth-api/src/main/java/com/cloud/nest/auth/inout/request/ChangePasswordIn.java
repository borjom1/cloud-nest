package com.cloud.nest.auth.inout.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import static com.cloud.nest.platform.model.validation.CommonRegexPattern.AT_LEAST_ONE_SPECIAL_CHAR;

public record ChangePasswordIn(

        @NotBlank
        @Pattern(
                regexp = AT_LEAST_ONE_SPECIAL_CHAR,
                message = "must contain at least one special character"
        )
        @Length(min = 6, max = 24)
        String oldPassword,

        @NotBlank
        @Pattern(
                regexp = AT_LEAST_ONE_SPECIAL_CHAR,
                message = "must contain at least one special character"
        )
        @Length(min = 6, max = 24)
        String newPassword

) {
}

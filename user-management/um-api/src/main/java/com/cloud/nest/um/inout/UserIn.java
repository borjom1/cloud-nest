package com.cloud.nest.um.inout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;

import static com.cloud.nest.platform.model.validation.CommonPattern.AT_LEAST_ONE_SPECIAL_CHAR;
import static com.cloud.nest.platform.model.validation.CommonPattern.CONTAINS_ONLY_LETTERS;

@Jacksonized
@Builder
public record UserIn(

        @NotBlank
        @Pattern(
                regexp = CONTAINS_ONLY_LETTERS,
                message = "can only contain letters"
        )
        @Length(min = 3, max = 32)
        String firstName,

        @NotBlank
        @Pattern(
                regexp = CONTAINS_ONLY_LETTERS,
                message = "can only contain letters"
        )
        @Length(min = 3, max = 32)
        String lastName,

        @NotBlank
        @Pattern(
                regexp = "^[a-zA-Z][a-zA-Z0-9_]*$",
                message = "can only start with a letter and contain only letters, digits and underscores"
        )
        @Length(min = 3, max = 16)
        String username,

        @NotBlank
        @Pattern(
                regexp = AT_LEAST_ONE_SPECIAL_CHAR,
                message = "must contain at least one special character"
        )
        @Length(min = 6, max = 24)
        String password

) {
}

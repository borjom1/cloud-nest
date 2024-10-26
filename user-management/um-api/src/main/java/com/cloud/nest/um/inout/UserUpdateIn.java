package com.cloud.nest.um.inout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;

import static com.cloud.nest.platform.model.validation.CommonPattern.ONLY_LETTERS;

@Jacksonized
@Builder
public record UserUpdateIn(

        @NotBlank
        @Pattern(
                regexp = ONLY_LETTERS,
                message = "can only contain letters"
        )
        @Length(min = 3, max = 32)
        String firstName,

        @NotBlank
        @Pattern(
                regexp = ONLY_LETTERS,
                message = "can only contain letters"
        )
        @Length(min = 3, max = 32)
        String lastName

) {
}

package com.cloud.nest.fm.inout.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.Length;

import static com.cloud.nest.platform.model.validation.CommonRegexPattern.FILENAME_REGEX;
import static com.cloud.nest.platform.model.validation.CommonRegexPattern.FILE_EXTENSION_REGEX;

@Builder
@Jacksonized
public record FileMetaIn(

        @NotBlank
        @Pattern(
                regexp = FILENAME_REGEX,
                message = "can contain only letters, numbers and underscores"
        )
        @Length(min = 1, max = 64)
        String filename,

        @Nullable
        @Pattern(
                regexp = FILE_EXTENSION_REGEX,
                message = "can contain only letters and numbers"
        )
        @Length(min = 1, max = 8)
        String ext

) {
}

package com.cloud.nest.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;

@With
@Jacksonized
@Builder
public record Token(

        @NotBlank
        @JsonProperty("sub")
        String subject,

        @NotBlank
        @JsonProperty("iss")
        String issuer,

        @NotNull
        @JsonProperty("iat")
        Instant issuedAt,

        @NotNull
        @JsonProperty("exp")
        Instant expireAt,

        @NotNull
        @JsonProperty("auths")
        List<TokenAuthority> authorities

) {
}

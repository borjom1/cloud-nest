package com.cloud.nest.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.Set;

@Setter
@Getter
@Jacksonized
@SuperBuilder
public class AccessToken {

    @NotBlank
    @JsonProperty("sub")
    private String subject;

    @NotBlank
    @JsonProperty("iss")
    private String issuer;

    @NotNull
    @JsonProperty("iat")
    private Instant issuedAt;

    @NotNull
    @JsonProperty("exp")
    private Instant expireAt;

    @NotNull
    @JsonProperty("auths")
    private Set<TokenAuthority> authorities;

}

package com.cloud.nest.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.HashSet;

@Setter
@Getter
@Jacksonized
@SuperBuilder
public class RefreshToken extends AccessToken {

    @NotBlank
    @JsonProperty("cip")
    private String clientIp;

    @NotBlank
    @JsonProperty("sub2")
    private String subjectV2;

    @NotBlank
    @JsonProperty("sub3")
    private Long subjectV3;

    @NotNull
    public static RefreshToken of(@NotNull AccessToken accessToken) {
        return RefreshToken.builder()
                .subject(accessToken.getSubject())
                .issuer(accessToken.getIssuer())
                .issuedAt(accessToken.getIssuedAt())
                .expireAt(accessToken.getExpireAt())
                .authorities(new HashSet<>(accessToken.getAuthorities()))
                .build();
    }

}

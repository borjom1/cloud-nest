package com.cloud.nest.auth.jwt;

import com.cloud.nest.auth.model.AccessToken;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface TokenSerializer {

    @NotBlank
    String serializeToken(@NotNull AccessToken token);

}

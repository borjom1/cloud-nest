package com.cloud.nest.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cloud.nest.auth.model.Token;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import static com.cloud.nest.platform.infrastructure.common.ObjectMapperUtils.toJson;

public class TokenSerializerImpl implements TokenSerializer {

    private final ObjectMapper objectMapper;
    private final Algorithm algorithm;

    public TokenSerializerImpl(
            @NotNull ObjectMapper objectMapper,
            @NotNull KeyContextHolder keyContextHolder
    ) {
        this.objectMapper = objectMapper;
        this.algorithm = Algorithm.RSA256(
                keyContextHolder.getPublicKey(),
                keyContextHolder.getPrivateKey()
        );
    }

    @NotBlank
    @Override
    public String serializeToken(@NotNull Token token) {
        return JWT.create()
                .withPayload(toJson(objectMapper, token))
                .sign(algorithm);
    }

}

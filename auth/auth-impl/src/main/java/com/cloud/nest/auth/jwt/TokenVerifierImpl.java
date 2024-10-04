package com.cloud.nest.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cloud.nest.auth.model.Token;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.Base64;

public class TokenVerifierImpl implements TokenVerifier {

    private final ObjectMapper objectMapper;
    private final JWTVerifier jwtVerifier;

    public TokenVerifierImpl(
            @NotNull JwtProperties jwtProperties,
            @NotNull ObjectMapper objectMapper,
            @NotNull KeyContextHolder keyContextHolder
    ) {
        this.objectMapper = objectMapper;
        this.jwtVerifier = initJwtVerifier(keyContextHolder, jwtProperties);
    }

    @Override
    public Token verifyAccessToken(String accessToken) {
        try {
            final DecodedJWT decodedJWT = jwtVerifier.verify(accessToken);
            return objectMapper.readValue(Base64.getDecoder().decode(decodedJWT.getPayload()), Token.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot parse access token payload", e);
        } catch (JWTVerificationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @NotNull
    private JWTVerifier initJwtVerifier(@NotNull KeyContextHolder holder, @NotNull JwtProperties jwtProperties) {
        final Algorithm alg = Algorithm.RSA256(holder.getPublicKey(), holder.getPrivateKey());
        return JWT.require(alg)
                .withIssuer(jwtProperties.getIssuer())
                .build();
    }

}

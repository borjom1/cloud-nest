package com.cloud.nest.auth.impl;

import com.cloud.nest.auth.AuthApiInternal;
import com.cloud.nest.auth.inout.NewAuthUserIn;
import com.cloud.nest.auth.inout.SessionOut;
import com.cloud.nest.platform.infrastructure.auth.BearerUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

public class AuthApiInternalStandalone implements AuthApiInternal {

    public static final String BASE_URL = "/internal/v1/auth";
    public static final String URL_USERS = "/users";

    private final WebClient webClient;

    public AuthApiInternalStandalone(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public CompletableFuture<Void> createUser(final NewAuthUserIn in) {
        return webClient.post()
                .uri(BASE_URL + URL_USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(in))
                .retrieve()
                .bodyToMono(Void.class)
                .toFuture();
    }

    @Override
    public CompletableFuture<SessionOut> authorize(final String accessToken) {
        return webClient.post()
                .uri(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, BearerUtils.addBearerPrefix(accessToken))
                .retrieve()
                .bodyToMono(SessionOut.class)
                .toFuture();
    }

}

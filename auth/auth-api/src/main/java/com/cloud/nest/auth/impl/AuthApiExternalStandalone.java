package com.cloud.nest.auth.impl;

import com.cloud.nest.auth.AuthApiExternal;
import com.cloud.nest.auth.inout.UserAuthIn;
import com.cloud.nest.auth.inout.AccessTokenOut;
import com.cloud.nest.platform.infrastructure.response.ComplexResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

public class AuthApiExternalStandalone implements AuthApiExternal {

    public static final String BASE_URL = "/external/v1/auth";

    private final WebClient webClient;

    public AuthApiExternalStandalone(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public CompletableFuture<ComplexResponse<AccessTokenOut>> authenticateUser(final UserAuthIn in) {
        return webClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(in))
                .exchangeToMono(clientResponse -> Mono.just(
                                ComplexResponse.of(
                                        clientResponse.bodyToMono(AccessTokenOut.class).block(),
                                        clientResponse.cookies()
                                )
                        )
                )
                .toFuture();
    }

}

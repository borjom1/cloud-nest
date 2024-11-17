package com.cloud.nest.um.impl;

import com.cloud.nest.um.UserApiInternal;
import com.cloud.nest.um.inout.UserOut;
import com.cloud.nest.um.inout.UserUpdateIn;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class UserApiInternalStandalone implements UserApiInternal {

    public static final String URL_USERS = "/internal/v1/users";

    public static final String PARAM_ID = "id";

    public static final String PATH_ID = "/{" + PARAM_ID + "}";

    private final WebClient webClient;

    public UserApiInternalStandalone(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public CompletableFuture<UserOut> findById(Long id) {
        return webClient.get()
                .uri(URL_USERS + PATH_ID, id)
                .retrieve()
                .bodyToMono(UserOut.class)
                .toFuture();
    }

    @Override
    public CompletableFuture<Void> updateUser(final Long id, final UserUpdateIn in) {
        return webClient.patch()
                .uri(URL_USERS + PATH_ID, id)
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(in))
                .retrieve()
                .bodyToMono(Void.class)
                .toFuture();
    }

}

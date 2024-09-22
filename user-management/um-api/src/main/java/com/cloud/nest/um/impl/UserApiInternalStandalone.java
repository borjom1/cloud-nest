package com.cloud.nest.um.impl;

import com.cloud.nest.um.UserApiInternal;
import com.cloud.nest.um.inout.UserUpdateIn;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.um.UserManagementApiConfig.WEB_CLIENT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class UserApiInternalStandalone implements UserApiInternal {

    public static final String URL_USERS = "/internal/v1/users";

    public static final String PARAM_ID = "id";

    public static final String PATH_ID = "/{" + PARAM_ID + "}";

    private final WebClient webClient;

    public UserApiInternalStandalone(@Qualifier(WEB_CLIENT) WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public CompletableFuture<Void> updateUser(Long id, UserUpdateIn in) {
        return webClient.patch()
                .uri(URL_USERS + PATH_ID, id)
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(in))
                .retrieve()
                .bodyToMono(Void.class)
                .toFuture();
    }

}

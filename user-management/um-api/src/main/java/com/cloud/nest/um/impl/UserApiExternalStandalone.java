package com.cloud.nest.um.impl;

import com.cloud.nest.um.UserApiExternal;
import com.cloud.nest.um.inout.UserIn;
import com.cloud.nest.um.inout.UserOut;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.um.UserManagementApiConfig.WEB_CLIENT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class UserApiExternalStandalone implements UserApiExternal {

    public static final String URL_USERS = "/external/v1/users";

    public static final String PARAM_ID = "id";

    public static final String PATH_ID = "/{" + PARAM_ID + "}";

    private final WebClient webClient;

    public UserApiExternalStandalone(@Qualifier(WEB_CLIENT) WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public CompletableFuture<UserOut> createUser(@NotNull UserIn in) {
        return webClient.post()
                .uri(URL_USERS)
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(in))
                .retrieve()
                .bodyToMono(UserOut.class)
                .toFuture();
    }

    @Override
    public CompletableFuture<UserOut> findById(Long id) {
        return webClient.get()
                .uri(URL_USERS + PATH_ID, id)
                .retrieve()
                .bodyToMono(UserOut.class)
                .toFuture();
    }

}

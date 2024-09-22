package com.cloud.nest.um.impl;

import com.cloud.nest.um.UserApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.um.UserManagementApiConfig.WEB_CLIENT;

public class UserApiStandalone implements UserApi {

    public static final String URL_USERS = "/users";

    private final WebClient webClient;

    public UserApiStandalone(@Qualifier(WEB_CLIENT) WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public CompletableFuture<String> getUserString() {
        return webClient.get()
                .uri("/user")
                .retrieve()
                .bodyToMono(String.class)
                .toFuture();
    }

}

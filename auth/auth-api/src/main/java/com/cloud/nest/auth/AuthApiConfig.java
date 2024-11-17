package com.cloud.nest.auth;

import com.cloud.nest.auth.impl.AuthApiInternalStandalone;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AuthApiConfig {

    public static final String SERVICE_ID = "auth-service";
    public static final String WEB_CLIENT = "authWebClient";

    @Bean(WEB_CLIENT)
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().baseUrl(getServiceBaseUrl());
    }

    @Bean
    public AuthApiInternal authApiInternal(@Qualifier(WEB_CLIENT) WebClient.Builder webClientBuilder) {
        return new AuthApiInternalStandalone(webClientBuilder);
    }

    private String getServiceBaseUrl() {
        return "http://%s".formatted(SERVICE_ID);
    }

}

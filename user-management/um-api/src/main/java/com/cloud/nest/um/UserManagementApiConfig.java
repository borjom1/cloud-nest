package com.cloud.nest.um;

import com.cloud.nest.um.impl.UserApiExternalStandalone;
import com.cloud.nest.um.impl.UserApiInternalStandalone;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class UserManagementApiConfig {

    public static final String SERVICE_ID = "user-management-service";
    public static final String WEB_CLIENT = "userManagementWebClient";

    @Bean(WEB_CLIENT)
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().baseUrl(getServiceBaseUrl());
    }

    @Bean
    public UserApiExternal userApiExternal(@Qualifier(WEB_CLIENT) WebClient.Builder webClientBuilder) {
        return new UserApiExternalStandalone(webClientBuilder);
    }

    @Bean
    public UserApiInternal userApiInternal(@Qualifier(WEB_CLIENT) WebClient.Builder webClientBuilder) {
        return new UserApiInternalStandalone(webClientBuilder);
    }

    private String getServiceBaseUrl() {
        return "http://%s".formatted(SERVICE_ID);
    }

}

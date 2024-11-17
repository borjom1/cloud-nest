package com.cloud.nest.api.config;

import com.cloud.nest.api.filter.AuthorizationGatewayFilter;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class GatewayRouterConfig {

    private static final String REGEX_REPLACEMENT = "(?<remaining>.*)";
    private static final String REPLACEMENT = "/external/${remaining}";

    private final AuthorizationGatewayFilter authGatewayFilter;

    @Bean
    RouteLocator routeLocator(@NotNull RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-anonymous", r -> r
                        .path("/auth/v1/security/login", "/auth/v1/security/refresh")
                        .filters(f -> f.rewritePath("/auth" + REGEX_REPLACEMENT, REPLACEMENT))
                        .uri("lb://auth-service"))
                .route("auth", r -> r
                        .path("/auth/**")
                        .filters(f -> f
                                .filter(authGatewayFilter)
                                .rewritePath("/auth" + REGEX_REPLACEMENT, REPLACEMENT))
                        .uri("lb://auth-service"))
                .route("um-anonymous", r -> r
                        .method(HttpMethod.POST)
                        .and()
                        .path("/um/v1/users")
                        .filters(f -> f.rewritePath("/um" + REGEX_REPLACEMENT, REPLACEMENT))
                        .uri("lb://user-management-service")
                )
                .route("um", r -> r
                        .path("/um/**")
                        .filters(f -> f
                                .filter(authGatewayFilter)
                                .rewritePath("/um" + REGEX_REPLACEMENT, REPLACEMENT))
                        .uri("lb://user-management-service")
                )
                .route("fm", r -> r
                        .path("/fm/**")
                        .filters(f -> f
                                .filter(authGatewayFilter)
                                .rewritePath("/fm" + REGEX_REPLACEMENT, REPLACEMENT))
                        .uri("lb://file-management-service"))
                .build();
    }

}

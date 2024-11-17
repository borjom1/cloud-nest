package com.cloud.nest.api.filter;

import com.cloud.nest.auth.AuthApiInternal;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.cloud.nest.platform.infrastructure.auth.BearerUtils;
import com.cloud.nest.platform.infrastructure.request.RequestUtils;
import com.cloud.nest.platform.model.ApiError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Base64;
import java.util.List;

import static com.cloud.nest.platform.infrastructure.common.ObjectMapperUtils.toJson;
import static com.cloud.nest.platform.infrastructure.request.RequestUtils.X_FORWARDED_FOR;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class AuthorizationGatewayFilter implements GatewayFilter {

    private final AuthApiInternal authApi;
    private final ObjectMapper objectMapper;

    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final List<String> headerValues = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

        if (headerValues == null || headerValues.isEmpty()) {
            return sendErrorResponse(
                    "Authorization header is missing",
                    HttpStatus.BAD_REQUEST,
                    exchange
            );
        }

        final String firstValue = headerValues.get(0);
        final String accessToken;
        try {
            accessToken = BearerUtils.removeBearerPrefix(firstValue);
        } catch (IllegalArgumentException e) {
            return sendErrorResponse(
                    "Invalid bearer token",
                    HttpStatus.BAD_REQUEST,
                    exchange
            );
        }

        final List<String> headerValue = request.getHeaders().get(X_FORWARDED_FOR);
        final String clientIp = requireNonNull(headerValue).get(0);

        return Mono.fromFuture(authApi.authorize(accessToken, clientIp))
                .flatMap(session -> chain.filter(attachSession(exchange, session)))
                .onErrorResume(err -> handleAuthorizationError(err, exchange));
    }

    @NotBlank
    private ServerWebExchange attachSession(@NotNull ServerWebExchange exchange, @NotNull UserAuthSession session) {
        final String jsonSession = toJson(objectMapper, session);
        final String encodedUserSession = Base64.getEncoder().encodeToString(jsonSession.getBytes(UTF_8));
        return exchange.mutate()
                .request(builder -> builder.header(RequestUtils.USER_SESSION_HEADER, encodedUserSession))
                .build();
    }

    @NotNull
    private Mono<Void> handleAuthorizationError(@NotNull Throwable err, @NotNull ServerWebExchange exchange) {
        if (err instanceof WebClientResponseException clientEx) {
            ApiError authApiError;
            try {
                authApiError = objectMapper.readValue(clientEx.getResponseBodyAsString(), ApiError.class);
            } catch (JsonProcessingException e) {
                authApiError = null;
            }
            return sendErrorResponse(
                    authApiError != null ? authApiError.error() : clientEx.getMessage(),
                    HttpStatus.valueOf(clientEx.getStatusCode().value()),
                    exchange
            );
        } else if (err instanceof NotFoundException notFoundEx) {
            return sendErrorResponse(
                    notFoundEx.getReason(),
                    HttpStatus.valueOf(notFoundEx.getStatusCode().value()),
                    exchange
            );
        }
        return sendErrorResponse(
                "Internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR,
                exchange
        );
    }


    @NotNull
    private Mono<Void> sendErrorResponse(
            @NotNull String errorMessage,
            @NotNull HttpStatus status,
            @NotNull ServerWebExchange exchange
    ) {
        final ServerHttpRequest request = exchange.getRequest();
        final ServerHttpResponse response = exchange.getResponse();
        final ApiError error = ApiError.builder()
                .requestId(request.getId())
                .status(status.value())
                .error(errorMessage)
                .uri(request.getURI().toString())
                .timestamp(Instant.now())
                .build();

        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        final String serializedError = toJson(objectMapper, error);
        final DataBuffer buffer = response.bufferFactory().wrap(serializedError.getBytes(UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

}

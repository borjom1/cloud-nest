package com.cloud.nest.api.filter;

import com.cloud.nest.platform.infrastructure.request.RequestUtils;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Objects;

@Component
public class RequestHeaderResolverGatewayFilter implements GlobalFilter, Ordered {

    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull GatewayFilterChain chain) {
        final InetSocketAddress socketAddress = exchange.getRequest().getRemoteAddress();
        Objects.requireNonNull(socketAddress, "InetSocketAddress must not be null");
        return chain.filter(exchange.mutate()
                .request(builder -> builder.header(RequestUtils.X_FORWARDED_FOR, socketAddress.getHostName()))
                .build());
    }

    @Override
    public int getOrder() {
        return -1;
    }

}

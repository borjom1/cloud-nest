package com.cloud.nest.platform.infrastructure.security;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;
import java.util.List;

public record AnonymousEndpoints(List<? extends RequestMatcher> matcherList) {

    public static AnonymousEndpoints none() {
        return of();
    }

    public static AnonymousEndpoints of(String... patterns) {
        return new AnonymousEndpoints(
                Arrays.stream(patterns).map(AntPathRequestMatcher::new).toList()
        );
    }

    public RequestMatcher[] matcherArray() {
        return matcherList.toArray(RequestMatcher[]::new);
    }

}

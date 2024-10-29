package com.cloud.nest.fm.impl;

import com.cloud.nest.fm.UserReportInternalApi;
import com.cloud.nest.fm.inout.response.UserReportOut;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserReportInternalApiStandalone implements UserReportInternalApi {

    public static final String URL_REPORTS = "/internal/v1/reports";
    public static final String URL_WEEKLY = "/weekly";
    public static final String URL_MONTHLY = "/monthly";
    public static final String URL_USERS = "/users";

    public static final String PARAM_ID = "id";
    public static final String PATH_ID = "/{" + PARAM_ID + "}";

    public static final ParameterizedTypeReference<List<UserReportOut>> REPORT_LIST_TYPE = new ParameterizedTypeReference<>() {
    };

    private final WebClient webClient;

    public UserReportInternalApiStandalone(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public CompletableFuture<List<UserReportOut>> getWeeklyStatisticsByUserId(Long userId) {
        return webClient.get()
                .uri(URL_REPORTS + URL_WEEKLY + URL_USERS + PATH_ID, userId)
                .retrieve()
                .bodyToMono(REPORT_LIST_TYPE)
                .toFuture();
    }

    @Override
    public CompletableFuture<List<UserReportOut>> getMonthlyStatisticsByUserId(Long userId) {
        return webClient.get()
                .uri(URL_REPORTS + URL_MONTHLY + URL_USERS + PATH_ID, userId)
                .retrieve()
                .bodyToMono(REPORT_LIST_TYPE)
                .toFuture();
    }

}

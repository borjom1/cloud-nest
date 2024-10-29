package com.cloud.nest.fm.controller;

import com.cloud.nest.fm.UserReportInternalApi;
import com.cloud.nest.fm.inout.response.UserReportOut;
import com.cloud.nest.fm.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.fm.impl.UserReportInternalApiStandalone.*;
import static java.util.concurrent.CompletableFuture.completedFuture;

@RestController
@RequestMapping(URL_REPORTS)
@RequiredArgsConstructor
public class UserReportInternalApiController implements UserReportInternalApi {

    private final ReportService reportService;

    @GetMapping(URL_WEEKLY + URL_USERS + PATH_ID)
    @Override
    public CompletableFuture<List<UserReportOut>> getWeeklyStatisticsByUserId(
            @PathVariable(PARAM_ID) Long userId
    ) {
        return completedFuture(reportService.getWeeklyReportsByUserId(userId));
    }

    @GetMapping(URL_MONTHLY + URL_USERS + PATH_ID)
    @Override
    public CompletableFuture<List<UserReportOut>> getMonthlyStatisticsByUserId(
            @PathVariable(PARAM_ID) Long userId
    ) {
        return completedFuture(reportService.getMonthlyReportsByUserId(userId));
    }

}

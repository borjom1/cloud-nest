package com.cloud.nest.fm;

import com.cloud.nest.fm.inout.response.UserReportOut;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserReportInternalApi {
    CompletableFuture<List<UserReportOut>> getWeeklyStatisticsByUserId(Long userId);

    CompletableFuture<List<UserReportOut>> getMonthlyStatisticsByUserId(Long userId);
}

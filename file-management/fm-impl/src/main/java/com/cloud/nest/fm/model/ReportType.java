package com.cloud.nest.fm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.Period;

import static java.time.LocalDateTime.now;

@RequiredArgsConstructor
@Getter
public enum ReportType {
    WEEKLY(Period.ofWeeks(1)),
    MONTHLY(Period.ofMonths(1));

    private final Period period;

    public ReportPeriod calculatePeriod() {
        final LocalDateTime periodEnd = now();
        final LocalDateTime periodStart = periodEnd.minus(period);
        return new ReportPeriod(periodStart, periodEnd);
    }

    public record ReportPeriod(LocalDateTime start, LocalDateTime end) {
    }

}

package com.cloud.nest.fm.service;

import com.cloud.nest.fm.inout.response.UserReportOut;
import com.cloud.nest.fm.persistence.repository.ReportRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    @Transactional(readOnly = true)
    @NotNull
    public List<UserReportOut> getWeeklyReportsByUserId(Long userId) {
        return reportRepository.findAllWeeklyByUserId(userId);
    }

    @Transactional(readOnly = true)
    @NotNull
    public List<UserReportOut> getMonthlyReportsByUserId(Long userId) {
        return reportRepository.findAllMonthlyByUserId(userId);
    }

}

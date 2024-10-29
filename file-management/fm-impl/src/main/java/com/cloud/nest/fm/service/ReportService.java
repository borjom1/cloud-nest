package com.cloud.nest.fm.service;

import com.cloud.nest.db.fm.tables.records.UserStorageRecord;
import com.cloud.nest.fm.inout.response.UserReportOut;
import com.cloud.nest.fm.mapper.ReportRecordMapper;
import com.cloud.nest.fm.persistence.repository.ReportRepository;
import com.cloud.nest.fm.persistence.repository.UserStorageRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.concurrent.TimeUnit.DAYS;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportService {

    private static final int BATCH_SIZE = 100;

    private final UserStorageRepository userStorageRepository;
    private final ReportRepository reportRepository;
    private final ReportRecordMapper reportRecordMapper;

    @Scheduled(fixedRate = 7L, timeUnit = DAYS)
    @Transactional(readOnly = true)
    void collectWeeklyStats() {
        log.info("Weekly user storage statistics collection is started");

        LocalDateTime periodEnd = now();
        collectUserStorageStat(periodEnd.minusDays(7L), periodEnd);

        log.info("Weekly user storage statistics collection is finished");
    }

    @Scheduled(fixedRate = 30L, timeUnit = DAYS)
    @Transactional(readOnly = true)
    void collectMonthlyStats() {
        log.info("Monthly user storage statistics collection is started");

        LocalDateTime periodEnd = now();
        collectUserStorageStat(periodEnd.minusDays(30L), periodEnd);

        log.info("Monthly user storage statistics collection is finished");
    }

    @Transactional(readOnly = true)
    @NotNull
    public List<UserReportOut> getWeeklyReportsByUserId(Long userId) {
        return reportRepository.findAllWeeklyByUserId(userId)
                .stream()
                .map(reportRecordMapper::toOut)
                .toList();
    }

    @Transactional(readOnly = true)
    @NotNull
    public List<UserReportOut> getMonthlyReportsByUserId(Long userId) {
        return reportRepository.findAllMonthlyByUserId(userId)
                .stream()
                .map(reportRecordMapper::toOut)
                .toList();
    }

    private void collectUserStorageStat(@NotNull LocalDateTime periodStart, @NotNull LocalDateTime periodEnd) {
        Long lastUserId = null;
        List<UserStorageRecord> fetchedRecords;
        do {
            fetchedRecords = userStorageRepository.findAll(lastUserId, BATCH_SIZE);
            if (fetchedRecords.isEmpty()) {
                break;
            }
            lastUserId = fetchedRecords.get(fetchedRecords.size() - 1).getUserId();

            fetchedRecords.stream()
                    .map(storageRecord -> reportRecordMapper.toRecord(storageRecord, periodStart, periodEnd))
                    .forEach(reportRepository::save);

        } while (true);
    }

}

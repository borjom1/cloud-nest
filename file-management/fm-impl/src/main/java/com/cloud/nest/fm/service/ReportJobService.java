package com.cloud.nest.fm.service;

import com.cloud.nest.db.fm.tables.records.ReportJobRecord;
import com.cloud.nest.db.fm.tables.records.UserStorageRecord;
import com.cloud.nest.fm.mapper.ReportJobMapper;
import com.cloud.nest.fm.mapper.ReportRecordMapper;
import com.cloud.nest.fm.model.ReportType;
import com.cloud.nest.fm.model.ReportType.ReportPeriod;
import com.cloud.nest.fm.persistence.repository.ReportJobRepository;
import com.cloud.nest.fm.persistence.repository.ReportRepository;
import com.cloud.nest.fm.persistence.repository.UserStorageRepository;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportJobService {

    private static final int BATCH_SIZE = 100;

    private final TransactionTemplate transactionTemplate;
    private final UserStorageRepository userStorageRepository;
    private final ReportRepository reportRepository;
    private final ReportJobRepository reportJobRepository;
    private final ReportRecordMapper reportRecordMapper;
    private final ReportJobMapper reportJobMapper;

    @PostConstruct
    public void initialize() {
        CompletableFuture.runAsync(this::checkPreviousReportJobsCompletion);
    }

    @Scheduled(cron = "0 0 * * 1 *")
    @Transactional
    void collectWeeklyStats() {
        executeReportJob(ReportType.WEEKLY);
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    void collectMonthlyStats() {
        executeReportJob(ReportType.MONTHLY);
    }

    private void checkPreviousReportJobsCompletion() {
        Arrays.stream(ReportType.values())
                .map(reportType -> (Runnable) () -> {
                    try {
                        checkUncompletedReportJob(reportType);
                    } catch (Exception e) {
                        log.error("Failed to perform [%s] report job".formatted(reportType), e);
                    }
                })
                .forEach(CompletableFuture::runAsync);
    }

    private void checkUncompletedReportJob(ReportType reportType) {
        final ReportPeriod period = reportType.calculatePeriod();
        ReportJobRecord jobRecord = transactionTemplate.execute(ts -> {
            ReportJobRecord record = reportJobRepository.findLastByReportType(reportType);
            if (record != null) {
                if (period.start().isAfter(record.getCreated())) {
                    return createJob(reportType, period.end());
                } else if (record.getCompleted()) {
                    log.info("{} user storage report job is already completed", reportType);
                    return null;
                } else {
                    log.info("{} user storage report job is restored", reportType);
                }
            } else {
                record = createJob(reportType, period.end());
            }
            return record;
        });

        if (jobRecord != null) {
            executeReportJob(reportType, jobRecord);
        }
    }

    private void executeReportJob(ReportType reportType) {
        final ReportPeriod period = reportType.calculatePeriod();
        final ReportJobRecord jobRecord = createJob(reportType, period.end());
        executeReportJob(reportType, jobRecord);
    }

    private void executeReportJob(@NotNull ReportType reportType, @NotNull ReportJobRecord jobRecord) {
        log.info("{} user storage report job is started", reportType);

        final PartialJobData jobData = new PartialJobData(jobRecord, jobRecord.getLastReportedUserId());
        while (true) {
            final PartialJobResult result = transactionTemplate.execute(
                    ts2 -> executePartialReportJob(jobData)
            );
            if (result == null || result.completed()) {
                log.info("{} user storage report job is completed", reportType);
                break;
            }
            jobData.setLastUserId(result.lastUserId());
        }

        jobRecord.setCompleted(true);
        transactionTemplate.executeWithoutResult(ts -> reportJobRepository.save(jobRecord));
    }

    private PartialJobResult executePartialReportJob(PartialJobData jobData) {
        Long lastUserId = jobData.getLastUserId();
        List<UserStorageRecord> fetchedRecords = userStorageRepository.findAll(lastUserId, BATCH_SIZE);
        if (fetchedRecords.isEmpty()) {
            return new PartialJobResult(true, lastUserId);
        }

        lastUserId = fetchedRecords.get(fetchedRecords.size() - 1).getUserId();
        fetchedRecords.stream()
                .map(storageRecord -> reportRecordMapper.toRecord(jobData.getRecord().getId(), storageRecord))
                .forEach(reportRepository::save);

        final ReportJobRecord record = jobData.getRecord();
        record.setLastReportedUserId(lastUserId);
        reportJobRepository.save(record);

        return new PartialJobResult(false, lastUserId);
    }

    private ReportJobRecord createJob(ReportType reportType, LocalDateTime periodEnd) {
        return reportJobRepository.save(reportJobMapper.toRecord(periodEnd, reportType));
    }

    @AllArgsConstructor
    @Getter
    @Setter
    static class PartialJobData {

        private ReportJobRecord record;

        @Nullable
        private Long lastUserId;

    }

    record PartialJobResult(boolean completed, Long lastUserId) {
    }

}

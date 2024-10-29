package com.cloud.nest.fm.mapper;

import com.cloud.nest.db.fm.tables.records.ReportRecord;
import com.cloud.nest.db.fm.tables.records.UserStorageRecord;
import com.cloud.nest.fm.inout.response.UserReportOut;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReportRecordMapper {

    @Mapping(target = "downloadedBytes", source = "record.totalDownloadedBytes")
    @Mapping(target = "uploadedBytes", source = "record.totalUploadedBytes")
    ReportRecord toRecord(UserStorageRecord record, LocalDateTime periodStart, LocalDateTime periodEnd);

    UserReportOut toOut(ReportRecord record);

}

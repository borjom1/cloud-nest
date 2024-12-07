package com.cloud.nest.fm.mapper;

import com.cloud.nest.db.fm.tables.records.ReportRecord;
import com.cloud.nest.db.fm.tables.records.UserStorageRecord;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReportRecordMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reportJobId", source = "reportJobId")
    @Mapping(target = "downloadedBytes", source = "record.totalDownloadedBytes")
    @Mapping(target = "uploadedBytes", source = "record.totalUploadedBytes")
    ReportRecord toRecord(Long reportJobId, UserStorageRecord record);
}

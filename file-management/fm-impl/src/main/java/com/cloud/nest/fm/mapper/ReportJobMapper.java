package com.cloud.nest.fm.mapper;

import com.cloud.nest.db.fm.tables.records.ReportJobRecord;
import com.cloud.nest.fm.model.ReportType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReportJobMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastReportedUserId", ignore = true)
    @Mapping(target = "created", source = "created")
    @Mapping(target = "updated", source = "created")
    @Mapping(target = "completed", expression = "java(true)")
    @Mapping(target = "type", expression = "java(type.name())")
    ReportJobRecord toRecord(LocalDateTime created, ReportType type);

}

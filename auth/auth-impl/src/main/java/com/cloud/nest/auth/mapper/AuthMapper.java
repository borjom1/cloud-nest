package com.cloud.nest.auth.mapper;

import com.cloud.nest.auth.inout.NewAuthUserIn;
import com.cloud.nest.auth.inout.SessionOut;
import com.cloud.nest.db.auth.tables.records.SessionRecord;
import com.cloud.nest.db.auth.tables.records.UserRecord;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AuthMapper {

    SessionOut toOut(SessionRecord record);

    @Mapping(target = "created", source = "dateTime")
    @Mapping(target = "updated", source = "dateTime")
    @Mapping(target = "id", source = "in.userId")
    UserRecord toRecord(NewAuthUserIn in, LocalDateTime dateTime);

}

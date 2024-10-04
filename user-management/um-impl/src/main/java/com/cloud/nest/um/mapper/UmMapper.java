package com.cloud.nest.um.mapper;

import com.cloud.nest.db.um.tables.records.UserRecord;
import com.cloud.nest.um.inout.UserIn;
import com.cloud.nest.um.inout.UserInternalOut;
import com.cloud.nest.um.inout.UserOut;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UmMapper {
    UserOut toOut(UserRecord record);

    UserInternalOut toUserInternal(UserRecord record);

    @Mapping(target = "created", source = "dateTime")
    @Mapping(target = "updated", source = "dateTime")
    UserRecord toRecord(UserIn in, LocalDateTime dateTime);
}

package com.cloud.nest.auth.mapper;

import com.cloud.nest.auth.inout.SessionStatus;
import com.cloud.nest.auth.inout.request.NewAuthUserIn;
import com.cloud.nest.auth.inout.response.ActiveSessionOut;
import com.cloud.nest.auth.inout.response.SessionHistoryOut;
import com.cloud.nest.auth.model.SessionCreate;
import com.cloud.nest.db.auth.tables.records.SessionHistoryRecord;
import com.cloud.nest.db.auth.tables.records.SessionRecord;
import com.cloud.nest.db.auth.tables.records.UserRecord;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AuthMapper {

    @Mapping(target = "table", ignore = true)
    @Mapping(target = "qualifier", ignore = true)
    @Mapping(target = "created", source = "dateTime")
    @Mapping(target = "updated", source = "dateTime")
    @Mapping(target = "id", source = "in.userId")
    UserRecord toRecord(NewAuthUserIn in, LocalDateTime dateTime);

    @Mapping(target = "table", ignore = true)
    @Mapping(target = "qualifier", ignore = true)
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "userId", source = "sessionCreate.userId")
    @Mapping(target = "clientIp", source = "sessionCreate.requestDetails.clientIp")
    @Mapping(target = "userAgent", source = "sessionCreate.requestDetails.userAgent")
    @Mapping(target = "username", source = "sessionCreate.username")
    @Mapping(target = "lastActive", source = "now")
    @Mapping(target = "created", source = "now")
    @Mapping(target = "updated", source = "now")
    @Mapping(target = "status", expression = "java(AuthMapper.getActiveStatus().name())")
    SessionRecord toSessionRecord(
            SessionCreate sessionCreate,
            LocalDateTime now,
            LocalDateTime expiresAt,
            String jsonProperties
    );

    ActiveSessionOut toActiveSessionOut(SessionRecord record);

    @Mapping(target = "sessionId", source = "record.id")
    SessionHistoryRecord toSessionHistoryRecord(SessionRecord record);

    SessionHistoryOut toSessionHistoryOut(SessionHistoryRecord record);

    static SessionStatus getActiveStatus() {
        return SessionStatus.ACTIVE;
    }

}

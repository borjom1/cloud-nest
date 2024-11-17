package com.cloud.nest.auth.mapper;

import com.cloud.nest.auth.inout.request.NewAuthUserIn;
import com.cloud.nest.auth.inout.SessionStatus;
import com.cloud.nest.auth.inout.response.SessionHistoryOut;
import com.cloud.nest.auth.model.SessionProperties;
import com.cloud.nest.db.auth.tables.records.SessionHistoryRecord;
import com.cloud.nest.db.auth.tables.records.SessionRecord;
import com.cloud.nest.db.auth.tables.records.UserRecord;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AuthMapper {

    @Mapping(target = "sessionId", source = "id")
    UserAuthSession toOut(SessionRecord record);

    @Mapping(target = "created", source = "dateTime")
    @Mapping(target = "updated", source = "dateTime")
    @Mapping(target = "id", source = "in.userId")
    UserRecord toRecord(NewAuthUserIn in, LocalDateTime dateTime);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "userId", source = "sessionProperties.userId")
    @Mapping(target = "clientIp", source = "sessionProperties.requestDetails.clientIp")
    @Mapping(target = "userAgent", source = "sessionProperties.requestDetails.clientAgent")
    @Mapping(target = "username", source = "sessionProperties.username")
    @Mapping(target = "lastActive", source = "now")
    @Mapping(target = "created", source = "now")
    @Mapping(target = "updated", source = "now")
    @Mapping(target = "status", expression = "java(AuthMapper.getActiveStatus().name())")
    SessionRecord toSessionRecord(
            SessionProperties sessionProperties,
            LocalDateTime now,
            LocalDateTime expiresAt
    );

    @Mapping(target = "id", ignore = true)
    SessionHistoryRecord toSessionHistoryRecord(SessionRecord record);

    SessionHistoryOut toSessionHistoryOut(SessionHistoryRecord record);

    static SessionStatus getActiveStatus() {
        return SessionStatus.ACTIVE;
    }

}

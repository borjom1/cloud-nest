package com.cloud.nest.auth.inout.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

public record ActiveSessionOut(
        String clientIp,
        String userAgent,
        @JsonFormat(shape = STRING) LocalDateTime created,
        @JsonFormat(shape = STRING) LocalDateTime lastActive
) {
}

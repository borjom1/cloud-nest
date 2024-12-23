package com.cloud.nest.platform.model.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
public enum UserRole {
    USER(UserRoleCodes.USER);

    private final String code;

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    @Nullable
    public static UserRole fromCode(@Nullable String code) {
        if (ObjectUtils.isEmpty(code)) {
            return null;
        }
        for (var v : values()) {
            if (v.getCode().equals(code)) {
                return v;
            }
        }
        return null;
    }

    @UtilityClass
    public static class UserRoleCodes {

        public static final String USER = "USER";

    }

}

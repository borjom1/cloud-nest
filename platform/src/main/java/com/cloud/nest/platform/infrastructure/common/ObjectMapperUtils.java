package com.cloud.nest.platform.infrastructure.common;

import com.cloud.nest.platform.model.exception.UnexpectedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectMapperUtils {

    @NotBlank
    public static String toJson(@NotNull ObjectMapper objectMapper, @NotNull Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new UnexpectedException("Cannot serialize object [%s] to JSON".formatted(object.getClass().getName()), e);
        }
    }

}

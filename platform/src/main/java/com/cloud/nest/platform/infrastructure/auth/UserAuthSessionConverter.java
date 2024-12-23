package com.cloud.nest.platform.infrastructure.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@RequiredArgsConstructor
public class UserAuthSessionConverter implements Converter<String, UserAuthSession> {

    private final ObjectMapper objectMapper;

    @Override
    public UserAuthSession convert(@Nonnull String source) {
        final byte[] jsonSession = Base64.getDecoder().decode(source.getBytes(UTF_8));
        try {
            return objectMapper.readValue(jsonSession, UserAuthSession.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

}

package com.cloud.nest.platform.infrastructure.response;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMap;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ComplexResponse<T> {

    @Nullable
    private T responseBody;

    private Collection<ResponseCookie> cookies;

    @NotNull
    public static <T> ComplexResponse<T> of(T body, @NotNull ResponseCookie cookie) {
        return new ComplexResponse<>(body, List.of(cookie));
    }

    @NotNull
    public static <T> ComplexResponse<T> of(T body, @NotNull MultiValueMap<String, ResponseCookie> multiValueMap) {
        return new ComplexResponse<>(body, multiValueMap.toSingleValueMap().values());
    }

}

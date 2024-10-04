package com.cloud.nest.auth.controller;

import com.cloud.nest.platform.infrastructure.response.ComplexResponse;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

@RestControllerAdvice
public class AuthResponseBodyHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            @Nonnull MethodParameter returnType,
            @Nonnull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            @Nullable Object body,
            @Nonnull MethodParameter returnType,
            @Nonnull MediaType selectedContentType,
            @Nonnull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @Nonnull ServerHttpRequest request,
            @Nonnull ServerHttpResponse response
    ) {
        if (body instanceof ComplexResponse<?> cp) {
            body = cp.getResponseBody();
            if (cp.getCookies() != null) {
                if (response instanceof HttpServletResponse hsr) {
                    cp.getCookies().forEach(cookie -> hsr.setHeader(SET_COOKIE, cookie.toString()));
                } else if (response instanceof ServletServerHttpResponse sshr) {
                    cp.getCookies().forEach(cookie -> sshr.getHeaders().add(SET_COOKIE, cookie.toString()));
                }
            }
        }
        return body;
    }

}

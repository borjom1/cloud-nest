package com.cloud.nest.platform.infrastructure.security;

import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.cloud.nest.platform.infrastructure.request.RequestUtils;
import com.cloud.nest.platform.model.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Log4j2
@RequiredArgsConstructor
public class UserSessionAuthenticationFilter extends OncePerRequestFilter {

    private static final String ROLE_PREFIX = "ROLE_";

    private final RequestMatcher requestMatcher = new AntPathRequestMatcher("/external/**");
    private final ObjectMapper objectMapper;
    private final Converter<String, UserAuthSession> sessionConverter;
    private final AnonymousEndpoints anonymousEndpoints;

    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        if (!requestMatcher.matches(request) ||
            anonymousEndpoints.matcherList().stream().anyMatch(m -> m.matches(request))) {
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Resolving user auth session for request-[{}]: {}", request.getRequestId(), request.getRequestURI());

        final String sessionHeader = request.getHeader(RequestUtils.USER_SESSION_HEADER);
        if (!StringUtils.hasText(sessionHeader)) {
            writeResponseError(request, response);
            return;
        }

        Authentication authentication;
        try {
            final UserAuthSession authSession = requireNonNull(sessionConverter.convert(sessionHeader), "session not present");
            authentication = UsernamePasswordAuthenticationToken.authenticated(
                    authSession,
                    null,
                    authSession.roles()
                            .stream()
                            .map(r -> new SimpleGrantedAuthority(ROLE_PREFIX + r.getCode()))
                            .collect(Collectors.toSet())
            );
        } catch (Exception e) {
            log.error("Cannot retrieve user session from request header", e);
            writeResponseError(request, response);
            return;
        }

        final SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(authentication);
        SecurityContextHolder.setContext(ctx);

        filterChain.doFilter(request, response);
    }

    private void writeResponseError(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        final ApiError error = ApiError.builder()
                .status(HttpServletResponse.SC_FORBIDDEN)
                .uri(request.getRequestURI())
                .error("Invalid authorization credentials")
                .build();
        objectMapper.writeValue(response.getOutputStream(), error);
    }

}

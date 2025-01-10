package com.cloud.nest.platform.infrastructure.security;

import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class CommonSecurityConfig {

    @Bean
    SecurityFilterChain filterChain(
            HttpSecurity httpSecurity,
            UserSessionAuthenticationFilter authenticationFilter,
            AnonymousEndpoints anonymousEndpoints
    ) throws Exception {
        return httpSecurity.formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(c -> c
                        .requestMatchers(anonymousEndpoints.matcherArray()).anonymous()
                        .requestMatchers("/actuator/health", "/error", "/internal/**").anonymous()
                        .requestMatchers("/external/**").authenticated())

                .addFilterBefore(authenticationFilter, AnonymousAuthenticationFilter.class)
                .build();
    }

    @Bean
    UserSessionAuthenticationFilter authenticationFilter(
            ObjectMapper objectMapper,
            Converter<String, UserAuthSession> converter,
            AnonymousEndpoints anonymousEndpoints
    ) {
        return new UserSessionAuthenticationFilter(objectMapper, converter, anonymousEndpoints);
    }

    @Bean
    @ConditionalOnMissingBean
    AnonymousEndpoints noneAnonymousEndpoints() {
        return AnonymousEndpoints.none();
    }

}

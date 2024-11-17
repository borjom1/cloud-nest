package com.cloud.nest.auth.config;

import com.cloud.nest.auth.AuthApiExternal;
import com.cloud.nest.auth.impl.AuthApiInternalStandalone;
import com.cloud.nest.auth.jwt.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
public class AuthSecurityConfig {

    @Bean
    SecurityFilterChain filterChain(@NotNull HttpSecurity security) throws Exception {
        return security
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(c -> c
                        .requestMatchers(AuthApiExternal.BASE_URL + "/**").permitAll()
                        .requestMatchers(POST, AuthApiInternalStandalone.BASE_URL + "/**").permitAll()
                        .requestMatchers(GET, "/actuator/health").permitAll()
                        .anyRequest().authenticated())

                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConfigurationProperties("security.jwt")
    JwtProperties jwtProperties() {
        return new JwtProperties();
    }

    @Bean
    KeyContextHolder jwtKeyContextHolder(JwtProperties properties) {
        return new KeyContextHolder(
                properties.getSign().getPrivateKey(),
                properties.getSign().getPublicKey()
        );
    }

    @Bean
    TokenSerializer tokenSerializer(ObjectMapper objectMapper, KeyContextHolder keyContextHolder) {
        return new TokenSerializerImpl(objectMapper, keyContextHolder);
    }

    @Bean
    TokenVerifier tokenVerifier(
            JwtProperties properties,
            ObjectMapper objectMapper,
            KeyContextHolder keyContextHolder
    ) {
        return new TokenVerifierImpl(properties, objectMapper, keyContextHolder);
    }

}

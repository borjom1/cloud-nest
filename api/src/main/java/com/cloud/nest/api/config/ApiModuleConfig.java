package com.cloud.nest.api.config;

import com.cloud.nest.auth.AuthApiConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AuthApiConfig.class)
public class ApiModuleConfig {

    @Bean
    ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

}

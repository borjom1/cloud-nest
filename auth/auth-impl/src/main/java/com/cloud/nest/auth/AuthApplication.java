package com.cloud.nest.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

@EnableTransactionManagement
@EnableScheduling
@SpringBootApplication
public class AuthApplication {
    public static void main(String... args) {
        Locale.setDefault(Locale.US);
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.systemDefault()));
        SpringApplication.run(AuthApplication.class);
    }
}
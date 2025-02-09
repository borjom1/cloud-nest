package com.cloud.nest.um;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
public class UserManagementApplication {
    public static void main(String... args) {
        Locale.setDefault(Locale.US);
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.systemDefault()));
        SpringApplication.run(UserManagementApplication.class);
    }
}

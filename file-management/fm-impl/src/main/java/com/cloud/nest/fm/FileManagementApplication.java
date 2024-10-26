package com.cloud.nest.fm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
public class FileManagementApplication {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.systemDefault()));
        SpringApplication.run(FileManagementApplication.class, args);
    }

}

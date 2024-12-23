package com.cloud.nest.fm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Data
@ConfigurationProperties("business.storage")
@EnableConfigurationProperties
public class StorageProperties {

    private UserStorageProperties user;
    private BinProperties bin;

    @Data
    public static class UserStorageProperties {
        private long defaultStorageSize;
    }

    @Data
    public static class BinProperties {
        private Duration clearRate;
    }

}

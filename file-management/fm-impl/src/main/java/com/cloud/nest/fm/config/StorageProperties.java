package com.cloud.nest.fm.config;

import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.time.Duration;

@Data
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

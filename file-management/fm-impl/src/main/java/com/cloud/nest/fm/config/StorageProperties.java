package com.cloud.nest.fm.config;

import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Data
@EnableConfigurationProperties
public class StorageProperties {

    private UserStorageProperties user;

    @Data
    public static class UserStorageProperties {
        private long defaultStorageSize;
    }

}

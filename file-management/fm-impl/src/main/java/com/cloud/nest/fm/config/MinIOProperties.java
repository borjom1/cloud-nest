package com.cloud.nest.fm.config;

import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Data
@EnableConfigurationProperties
public class MinIOProperties {

    private String url;
    private String username;
    private String password;
    private Buckets buckets;

    @Data
    public static class Buckets {
        private int partSize;
        private String defaultBucket;
    }

}

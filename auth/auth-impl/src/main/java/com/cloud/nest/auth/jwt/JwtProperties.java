package com.cloud.nest.auth.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.time.Duration;

@EnableConfigurationProperties
@Data
public class JwtProperties {

    private Duration accessTtl;
    private Duration refreshTtl;
    private String issuer;
    private SignatureProperties sign;

    @Data
    public static class SignatureProperties {
        private String privateKey;
        private String publicKey;
    }

}

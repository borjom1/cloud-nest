package com.cloud.nest.auth.jwt;

import com.cloud.nest.platform.model.exception.UnexpectedException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Getter
public class KeyContextHolder {

    private static final KeyFactory KEY_FACTORY;

    static {
        try {
            KEY_FACTORY = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new UnexpectedException("Cannot get KeyFactory RSA instance", e);
        }
    }

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public KeyContextHolder(@NotBlank String privateKeySource, @NotBlank String publicKeySource) {
        this.privateKey = readPrivateKey(privateKeySource);
        this.publicKey = readPublicKey(publicKeySource);
    }

    @NotNull
    private RSAPrivateKey readPrivateKey(@NotBlank String rawKeyContent) {
        final byte[] decodedPrivateKey = Base64.getDecoder().decode(rawKeyContent.getBytes(UTF_8));
        final KeySpec keySpec = new PKCS8EncodedKeySpec(decodedPrivateKey);
        try {
            return (RSAPrivateKey) KEY_FACTORY.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new UnexpectedException("Cannot generate RSA private key", e);
        }
    }

    @NotNull
    private RSAPublicKey readPublicKey(@NotBlank String rawKeyContent) {
        final byte[] decodedPrivateKey = Base64.getDecoder().decode(rawKeyContent.getBytes(UTF_8));
        final KeySpec keySpec = new X509EncodedKeySpec(decodedPrivateKey);
        try {
            return (RSAPublicKey) KEY_FACTORY.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new UnexpectedException("Cannot generate RSA public key", e);
        }
    }

}

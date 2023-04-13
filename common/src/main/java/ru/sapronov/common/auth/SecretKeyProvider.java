package ru.sapronov.common.auth;

import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Component
public class SecretKeyProvider {
    public Key getKey(String secretKey) {
        byte[] decodedKey = Base64.getEncoder().encode(secretKey.getBytes());
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }
}

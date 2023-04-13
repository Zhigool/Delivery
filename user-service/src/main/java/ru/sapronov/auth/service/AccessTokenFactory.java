package ru.sapronov.auth.service;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.sapronov.common.auth.Claims;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class AccessTokenFactory {

    private final String secretKey;

    public AccessTokenFactory(
            @Value("${delivery.security.jwt-secret-key}")
            String secretKey) {
        this.secretKey = secretKey;
    }

    public String createToken(String subjectEmail, Claims claims) {
        byte[] decodedKey = Base64.getEncoder().encode(secretKey.getBytes());
        Key key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
        return Jwts.builder()
                .setSubject(subjectEmail)
                .setExpiration(new Date(System.currentTimeMillis() + 900_000)) // 1 час
                .claim("userId", claims.id().toString())
                .claim("role", claims.role().toString())
                .signWith(key)
                .compact();
    }
}

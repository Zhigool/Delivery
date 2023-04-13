package ru.sapronov.common.auth;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccessTokenValidator {

    private final String secretKey;
    private final SecretKeyProvider secretKeyProvider;

    public AccessTokenValidator(
            @Value("${delivery.security.jwt-secret-key}")
            String secretKey,
            SecretKeyProvider secretKeyProvider) {
        this.secretKey = secretKey;
        this.secretKeyProvider = secretKeyProvider;
    }

    public Claims validAndExtractClaims(String accessToken) {
        io.jsonwebtoken.Claims body = Jwts.parserBuilder()
                .setSigningKey(secretKeyProvider.getKey(secretKey))
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
        UUID userId = UUID.fromString((String) body.get("userId"));
        PrincipalProvider.PrincipalRole role = PrincipalProvider.PrincipalRole.valueOf((String) body.get("role"));
        return new Claims(
                userId,
                role
        );
    }
}

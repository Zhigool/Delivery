package ru.sapronov.common.auth;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PrincipalProvider {

    private final ThreadLocal<Principal> principalStore = new ThreadLocal<>();

    public void  setPrincipal(UUID principalId, PrincipalRole role) {
        principalStore.set(new Principal(principalId, role));
    }

    public Optional<Principal> getPrincipal() {
        return Optional.ofNullable(principalStore.get());
    }

    public record Principal(UUID id, PrincipalRole role) {}

    public enum PrincipalRole {
        CLIENT, ADMIN, COURIER
    }
}

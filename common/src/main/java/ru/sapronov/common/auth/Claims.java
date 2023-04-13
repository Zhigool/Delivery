package ru.sapronov.common.auth;

import java.util.UUID;

public record Claims(UUID id, PrincipalProvider.PrincipalRole role) {}
package ru.sapronov.common.provider;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidProvider {
    public UUID getRandom() { return UUID.randomUUID(); }
}

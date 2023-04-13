package ru.sapronov.common.event.person;

import java.io.Serializable;
import java.util.UUID;

public record NewAccountWasCreatedEvent(
        String email,
        UUID confirmationToken
) implements Serializable {}

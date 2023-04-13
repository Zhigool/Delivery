package ru.sapronov.common.event.person;

import java.io.Serializable;
import java.util.UUID;

public record NewCourierWasAppointedEvent(
        UUID courierId,
        String firstName,
        String lastName
) implements Serializable {}

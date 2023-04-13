package ru.sapronov.common.event.order;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

public record OrderWasCanceledEvent(
        UUID orderId,
        UUID courierId,
        ZonedDateTime createdAt
) implements Serializable {}

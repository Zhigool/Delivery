package ru.sapronov.common.event.order;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

public record OrderWasCreatedEvent(
        UUID id,
        UUID orderId,
        UUID clientId,
        Double destinationLongitude,
        Double destinationLatitude,
        Double departureLongitude,
        Double departureLatitude,
        ZonedDateTime createdAt
) implements Serializable {}

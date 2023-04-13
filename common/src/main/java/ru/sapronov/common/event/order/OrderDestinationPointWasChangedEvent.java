package ru.sapronov.common.event.order;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

public record OrderDestinationPointWasChangedEvent(
        UUID orderId,
        Double destinationLongitude,
        Double destinationLatitude,
        ZonedDateTime createAt
) implements Serializable {}

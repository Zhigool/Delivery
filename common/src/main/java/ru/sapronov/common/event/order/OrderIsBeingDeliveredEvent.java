package ru.sapronov.common.event.order;

import java.io.Serializable;
import java.util.UUID;

public record OrderIsBeingDeliveredEvent(
        UUID orderId,
        UUID clientId,
        UUID courierId
) implements Serializable {}

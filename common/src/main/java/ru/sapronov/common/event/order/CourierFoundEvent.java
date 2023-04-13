package ru.sapronov.common.event.order;

import java.io.Serializable;
import java.util.UUID;

public record CourierFoundEvent(
        UUID orderId,
        UUID courierId
) implements Serializable {}

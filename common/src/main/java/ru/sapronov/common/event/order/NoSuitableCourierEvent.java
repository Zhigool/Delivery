package ru.sapronov.common.event.order;

import java.io.Serializable;
import java.util.UUID;

public record NoSuitableCourierEvent(
        UUID orderId
) implements Serializable {}

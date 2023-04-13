package ru.sapronov.order.dto;

import java.util.UUID;

public record AssignCourierToOrderRequestDto(
        UUID orderId,
        UUID courierId
) {}

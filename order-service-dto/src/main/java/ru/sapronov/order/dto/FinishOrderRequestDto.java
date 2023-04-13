package ru.sapronov.order.dto;

import java.util.UUID;

public record FinishOrderRequestDto(
        UUID orderId
) {}

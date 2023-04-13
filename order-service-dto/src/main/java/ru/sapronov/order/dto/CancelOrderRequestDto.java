package ru.sapronov.order.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CancelOrderRequestDto(
        @NotNull(message = "Order ID is mandatory!")
        UUID orderId
) {}

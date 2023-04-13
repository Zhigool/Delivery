package ru.sapronov.order.dto;

import ru.sapronov.common.dto.PointDto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record OrderDto(
        UUID orderId,
        UUID clientId,
        String status,
        CourierDto courier,
        PointDto destinationPoint,
        PointDto departurePoint,
        ZonedDateTime createdAt
) {}

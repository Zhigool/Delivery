package ru.sapronov.order.dto;

import jakarta.validation.constraints.NotNull;
import ru.sapronov.common.dto.PointDto;

public record CreateOrderRequestDto(
        @NotNull(message = "Destination point coordinates is mandatory")
        PointDto destinationPoint,
        @NotNull(message = "Departure point coordinates is mandatory")
        PointDto departurePoint
) {}

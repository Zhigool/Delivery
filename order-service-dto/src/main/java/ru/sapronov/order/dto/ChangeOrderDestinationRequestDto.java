package ru.sapronov.order.dto;

import jakarta.validation.constraints.NotNull;
import ru.sapronov.common.dto.PointDto;

import java.util.UUID;

public record ChangeOrderDestinationRequestDto(
        @NotNull(message = "Order ID is mandatory")
        UUID orderId,
        @NotNull(message = "Destination point coordinates is mandatory")
        PointDto newDestinationPoint
) {}

package ru.sapronov.order.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public record OrderListItemDto(
        UUID id,
        String status,
        ZonedDateTime finishedAt
) {}

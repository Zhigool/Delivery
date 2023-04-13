package ru.sapronov.order.dto;

import java.util.UUID;

public record CourierDto(
        UUID id,
        String firstname,
        String lastname
) {}

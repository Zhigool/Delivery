package ru.sapronov.common.dto;

import jakarta.validation.constraints.NotNull;

public record PointDto(
        @NotNull
        Double longitude,
        @NotNull
        Double latitude
) {}

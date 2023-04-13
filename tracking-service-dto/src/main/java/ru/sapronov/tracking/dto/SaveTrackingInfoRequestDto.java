package ru.sapronov.tracking.dto;

import jakarta.validation.constraints.NotNull;

public record SaveTrackingInfoRequestDto(
        @NotNull
        Double latitude,
        @NotNull
        Double longitude
) {}

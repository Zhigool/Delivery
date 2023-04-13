package ru.sapronov.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AppointCourierRequestDto(
        @NotBlank
        String email
) {}

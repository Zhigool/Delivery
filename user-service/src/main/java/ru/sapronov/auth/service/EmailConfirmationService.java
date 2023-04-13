package ru.sapronov.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sapronov.common.provider.UuidProvider;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailConfirmationService {
    private final UuidProvider uuidProvider;

    public UUID sendEmailWithConfirmation(String email) {
        return uuidProvider.getRandom();
    }
}

package ru.sapronov.auth.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordCryptor {
    private final BCryptPasswordEncoder encoder;

    public String salting(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean check(String passwordFromDb, String passwordFromUser) {
        return encoder.matches(passwordFromUser, passwordFromDb);
    }
}

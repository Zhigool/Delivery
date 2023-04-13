package ru.sapronov.confirmation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sapronov.confirmation.client.smtp.SMTPClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationService {

    private final SMTPClient smtpClient;

    public void sendConfirmationEmail(String email, UUID confirmationToken) {
        smtpClient.sendMessage(
                email,
                "email confirmation",
                String.format("Confirmation code: %s", confirmationToken.toString())
        );
    }
}

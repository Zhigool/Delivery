package ru.sapronov.confirmation;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.sapronov.common.event.person.NewAccountWasCreatedEvent;
import ru.sapronov.confirmation.service.ConfirmationService;

@Service
@RequiredArgsConstructor
public class EventListener {

    private final ConfirmationService confirmationService;

    @RabbitListener(queues = "new-account-was-created-confirmation-service-queue")
    public void handleNewAccountWasCreatedEvent(NewAccountWasCreatedEvent event) {
        confirmationService.sendConfirmationEmail(event.email(), event.confirmationToken());
    }
}

package ru.sapronov.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final RabbitTemplate rabbitTemplate;

    public void sendEventToExchange(Object event, String exchangeName) {
        rabbitTemplate.convertAndSend(exchangeName, "", event);
    }
}

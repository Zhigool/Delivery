package ru.sapronov.tracking;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.sapronov.common.event.order.OrderIsBeingDeliveredEvent;
import ru.sapronov.common.event.order.OrderWasCreatedEvent;
import ru.sapronov.tracking.service.OrderService;
import ru.sapronov.tracking.service.TrackingService;

@Service
@RequiredArgsConstructor
public class EventsListener {

    private final TrackingService trackingService;
    private final OrderService orderService;

    @RabbitListener(queues = "order-create-tracking-service-queue")
    public void handleCreateOrderEvent(OrderWasCreatedEvent event) {
        trackingService.findSuitableCourier(
                event.orderId(),
                event.departureLongitude(),
                event.departureLatitude()
        );
    }

    @RabbitListener(queues = "order-is-being-delivered-tracking-service-queue")
    public void handleOrderIsBeingDeliveredEvent(OrderIsBeingDeliveredEvent event) {
        orderService.saveOrderInfo(event.orderId(), event.clientId(), event.courierId());
    }
}

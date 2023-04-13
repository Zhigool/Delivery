package ru.sapronov.order;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.sapronov.common.event.order.CourierFoundEvent;
import ru.sapronov.common.event.order.NoSuitableCourierEvent;
import ru.sapronov.common.event.person.NewCourierWasAppointedEvent;
import ru.sapronov.order.dao.Courier;
import ru.sapronov.order.service.CourierService;
import ru.sapronov.order.service.OrderService;

@Service
@RequiredArgsConstructor
public class EventsListener {
    private final OrderService orderService;
    private final CourierService courierService;
    @RabbitListener(queues = "courier-found-order-service-queue")
    public void handleCourierFoundEvent(CourierFoundEvent event) {
        orderService.addToOrderCourierByEvent(
                event.orderId(),
                event.courierId()
        );
    }

    @RabbitListener(queues = "no-suitable-courier-order-service-queue")
    public void handleNoSuitableCourier(NoSuitableCourierEvent event) {
        orderService.cancelByEvent(event.orderId());
    }

    @RabbitListener(queues = "new-courier-was-appointed-order-service-queue")
    public void handleNewCourierWasAppointedEvent(NewCourierWasAppointedEvent event) {
        courierService.save(new Courier(
                event.courierId(),
                event.firstName(),
                event.lastName()
        ));
    }
}

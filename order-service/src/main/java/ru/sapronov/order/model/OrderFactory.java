package ru.sapronov.order.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sapronov.common.provider.DateProvider;
import ru.sapronov.order.model.state.OrderState;
import ru.sapronov.order.model.state.OrderStateRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderFactory {

    private final OrderStateRepository repository;
    private final DateProvider dateProvider;

    public Order create(
            UUID clientId,
            Point destinationPoint,
            Point departurePoint) {
        OrderState state = new OrderState(
                clientId,
                destinationPoint,
                departurePoint,
                dateProvider.now()
        );
        state = repository.save(state);
        return new Order(state);
    }
}

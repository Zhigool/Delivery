package ru.sapronov.order.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sapronov.order.model.state.OrderStateRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderRepository {

    private final OrderStateRepository orderStateRepository;

    public Optional<Order> findOrderById(UUID orderId) {
        return orderStateRepository.findById(orderId).map(Order::new);
    }

    public void save(Order order) {
        orderStateRepository.save(order.state());
    }

    public List<Order> findByClientId(UUID clientId) {
        return orderStateRepository.findAllByPersonId(clientId).stream().map(Order::new).toList();
    }

    public List<Order> findAll() {
        return orderStateRepository.findAll().stream().map(Order::new).toList();
    }

    public List<Order> findAllByCourierId(UUID courierId) {
        return orderStateRepository.findAllByDeliveryPersonId(courierId).stream().map(Order::new).toList();
    }
}

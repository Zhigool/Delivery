package ru.sapronov.tracking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sapronov.tracking.dao.Order;
import ru.sapronov.tracking.dao.OrderRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public void saveOrderInfo(UUID orderId, UUID clientId, UUID courierId) {
        orderRepository.save(new Order(
                orderId,
                clientId,
                courierId
        ));
    }

    public Optional<Order> findById(UUID orderId) {
        return orderRepository.findById(orderId);
    }
}

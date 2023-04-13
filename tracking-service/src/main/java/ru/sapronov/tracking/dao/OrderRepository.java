package ru.sapronov.tracking.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, UUID> {
    Optional<Order> findByCourierId(UUID courierId);
}

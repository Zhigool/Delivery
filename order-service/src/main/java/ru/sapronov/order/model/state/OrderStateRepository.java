package ru.sapronov.order.model.state;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface OrderStateRepository extends CrudRepository<OrderState, UUID> {

    List<OrderState> findAllByPersonId(UUID personId);
    List<OrderState> findAllByDeliveryPersonId(UUID deliveryPersonId);
    List<OrderState> findAll();
}

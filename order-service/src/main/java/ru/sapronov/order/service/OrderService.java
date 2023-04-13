package ru.sapronov.order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sapronov.common.auth.PrincipalProvider;
import ru.sapronov.common.event.EventService;
import ru.sapronov.common.event.order.OrderDestinationPointWasChangedEvent;
import ru.sapronov.common.event.order.OrderIsBeingDeliveredEvent;
import ru.sapronov.common.event.order.OrderWasCanceledEvent;
import ru.sapronov.common.event.order.OrderWasCreatedEvent;
import ru.sapronov.common.exception.AccessDeniedException;
import ru.sapronov.common.provider.DateProvider;
import ru.sapronov.common.provider.UuidProvider;
import ru.sapronov.order.event.ExchangeNames;
import ru.sapronov.order.exception.OrderNotFoundException;
import ru.sapronov.order.model.Order;
import ru.sapronov.order.model.OrderFactory;
import ru.sapronov.order.model.OrderRepository;
import ru.sapronov.order.model.Point;
import ru.sapronov.order.model.state.OrderState;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.sapronov.order.event.ExchangeNames.ORDER_IS_BEING_DELIVERED_EXCHANGE_NAME;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderFactory factory;
    private final EventService eventService;
    private final UuidProvider uuidProvider;
    private final DateProvider dateProvider;
    private final OrderRepository orderRepository;

    @Transactional
    public void createOrder(UUID clientId, Point destinationPoint, Point departurePoint) {
        Order order = factory.create(clientId, destinationPoint, departurePoint);
        eventService.sendEventToExchange(new OrderWasCreatedEvent(
                uuidProvider.getRandom(),
                order.getId(),
                clientId,
                destinationPoint.longitude(),
                destinationPoint.latitude(),
                departurePoint.longitude(),
                departurePoint.latitude(),
                order.getCreatedAt()
        ), ExchangeNames.ORDER_CREATED_EXCHANGE_NAME);
    }

    @Transactional
    public void addToOrderCourierByEvent(UUID orderId, UUID courierId) {
        Optional<Order> optionalOrder = orderRepository.findOrderById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.addCourier(courierId);
            orderRepository.save(order);
            eventService.sendEventToExchange(new OrderIsBeingDeliveredEvent(
                    orderId,
                    order.getClientId(),
                    courierId
            ), ORDER_IS_BEING_DELIVERED_EXCHANGE_NAME);
        } else {
            // TODO: 09.04.2023 добавить отбивку тут или в первой ветке отправку евента
        }
    }

    @Transactional
    public void cancelByEvent(UUID orderId) {
        Optional<Order> optionalOrder = orderRepository.findOrderById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.cancel(dateProvider.now());
            orderRepository.save(order);
        }
    }

    @Transactional
    public void changeOrderDestination(PrincipalProvider.Principal principal, UUID orderId, Point newDestinationPoint) {
        if (principal.role() != PrincipalProvider.PrincipalRole.CLIENT) { return; }
        Optional<Order> optionalOrder = orderRepository.findOrderById(orderId);
        if (optionalOrder.isEmpty()) { throw new OrderNotFoundException(); }
        Order order = optionalOrder.get();
        if (order.getStatus() == OrderState.OrderStatus.CANCELED
                || order.getStatus() == OrderState.OrderStatus.DELIVERED) { return; }
        UUID clientId = order.getClientId();
        if (!clientId.equals(principal.id())) { throw new OrderNotFoundException(); }

        order.changeDestination(newDestinationPoint);

        orderRepository.save(order);
        eventService.sendEventToExchange(new OrderDestinationPointWasChangedEvent(
                orderId,
                newDestinationPoint.longitude(),
                newDestinationPoint.latitude(),
                dateProvider.now()
        ), ExchangeNames.ORDER_DESTINATION_CHANGED_EXCHANGE_NAME);
    }

    @Transactional
    public void cancelByClient(PrincipalProvider.Principal principal, UUID orderId) {
        if (principal.role() == PrincipalProvider.PrincipalRole.COURIER) { return; }
        Optional<Order> optionalOrder = orderRepository.findOrderById(orderId);
        if (optionalOrder.isEmpty()) { throw new OrderNotFoundException(); }
        Order order = optionalOrder.get();
        if (principal.role() == PrincipalProvider.PrincipalRole.CLIENT
                && !order.getClientId().equals(principal.id())) { throw new OrderNotFoundException(); }
        if (order.getStatus() == OrderState.OrderStatus.CANCELED
                || order.getStatus() == OrderState.OrderStatus.DELIVERED) { return; }
        ZonedDateTime createdAt = dateProvider.now();
        order.cancel(createdAt);
        orderRepository.save(order);
        eventService.sendEventToExchange(new OrderWasCanceledEvent(
                orderId,
                order.getCourierId(),
                createdAt
        ), ExchangeNames.ORDER_CANCELED_EXCHANGE_NAME);
    }

    public Order getOrder(PrincipalProvider.Principal principal, UUID orderId) {
        Optional<Order> optionalOrder = orderRepository.findOrderById(orderId);
        if (optionalOrder.isEmpty()) { throw new OrderNotFoundException(); }
        Order order = optionalOrder.get();
        return switch (principal.role()) {
            case CLIENT -> {
                if (!order.getClientId().equals(principal.id())) { throw new OrderNotFoundException(); }
                yield order;
            }
            case COURIER -> {
                if (!order.getCourierId().equals(principal.id())) { throw new OrderNotFoundException(); }
                yield order;
            }
            case ADMIN -> order;
        };
    }

    public List<Order> findAllOrders(PrincipalProvider.Principal principal) {
        List<Order> orders = switch (principal.role()) {
            case CLIENT -> orderRepository.findByClientId(principal.id());
            case ADMIN -> orderRepository.findAll();
            case COURIER -> orderRepository.findAllByCourierId(principal.id());
        };
        return orders;
    }

    public void assignCourierToOrder(PrincipalProvider.Principal principal, UUID orderId, UUID courierId) {
        if (principal.role() != PrincipalProvider.PrincipalRole.ADMIN) { throw new OrderNotFoundException(); }
        Order order = orderRepository.findOrderById(orderId).orElseThrow(OrderNotFoundException::new);
        order.addCourier(courierId);
        orderRepository.save(order);
        eventService.sendEventToExchange(new OrderIsBeingDeliveredEvent(
                orderId,
                order.getClientId(),
                courierId
        ), ORDER_IS_BEING_DELIVERED_EXCHANGE_NAME);
    }

    public void finishOrder(PrincipalProvider.Principal principal, UUID orderId) {
        if (principal.role() != PrincipalProvider.PrincipalRole.COURIER) { throw new AccessDeniedException(); }
        Order order = orderRepository.findOrderById(orderId).orElseThrow(OrderNotFoundException::new);
        order.finish(dateProvider.now());
        orderRepository.save(order);
    }
}

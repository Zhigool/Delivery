package ru.sapronov.order.model;

import ru.sapronov.common.model.Stateful;
import ru.sapronov.order.model.state.OrderState;

import java.time.ZonedDateTime;
import java.util.UUID;

public record Order(OrderState state) implements Stateful<OrderState> {
    public UUID getId() {
        return state.getId();
    }

    public UUID getClientId() { return state.getPersonId(); }

    public ZonedDateTime getCreatedAt() {
        return state.getCreatedAt();
    }

    public ZonedDateTime getFinishedAt() {
        return state.getClosedAt();
    }

    public void addCourier(UUID courierId) {
        if (state.getDeliveryPersonId() != null) return;
        state.setDeliveryPersonId(courierId);
        state.setStatus(OrderState.OrderStatus.IS_BEING_DELIVERED);
    }

    public void cancel(ZonedDateTime closedAt) {
        state.setStatus(OrderState.OrderStatus.CANCELED);
        state.setClosedAt(closedAt);
    }

    public void changeDestination(Point newDestinationPoint) {
        state.setDestinationLatitude(newDestinationPoint.latitude());
        state.setDestinationLongitude(newDestinationPoint.longitude());
    }

    public OrderState.OrderStatus getStatus() {
        return state.getStatus();
    }

    public UUID getCourierId() { return state.getDeliveryPersonId(); }

    public Point getDesctinationPoint() {
        return new Point(
                state.getDestinationLongitude(),
                state.getDestinationLatitude()
        );
    }

    public Point getDeparturePoint() {
        return new Point(
                state.getDepartureLongitude(),
                state.getDepartureLatitude()
        );
    }

    public void finish(ZonedDateTime closedAt) {
        state.setStatus(OrderState.OrderStatus.DELIVERED);
        state.setClosedAt(closedAt);
    }
}

package ru.sapronov.order.model.state;


import jakarta.persistence.*;
import lombok.*;
import ru.sapronov.order.model.Point;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class OrderState {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID personId;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private UUID deliveryPersonId;
    private Double destinationLongitude;
    private Double destinationLatitude;
    private Double departureLongitude;
    private Double departureLatitude;
    private ZonedDateTime createdAt;
    private ZonedDateTime closedAt;

    public OrderState(
        UUID personId,
        Point destinationPoint,
        Point departurePoint,
        ZonedDateTime createdAt
    ) {
        this(
            null,
            personId,
            OrderStatus.CREATED,
            null,
            destinationPoint.longitude(),
            destinationPoint.latitude(),
            departurePoint.longitude(),
            departurePoint.latitude(),
            createdAt,
            null);
    }

    public enum OrderStatus {
        CREATED,
        IS_BEING_DELIVERED,
        DELIVERED,
        CANCELED
    }

    public OrderState copy() {
        return new OrderState(
            this.id,
            this.personId,
            this.status,
            this.deliveryPersonId,
            this.destinationLongitude,
            this.destinationLatitude,
            this.departureLongitude,
            this.departureLatitude,
            this.createdAt,
            this.closedAt
        );
    }
}

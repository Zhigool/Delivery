package ru.sapronov.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sapronov.common.auth.PrincipalProvider;
import ru.sapronov.common.dto.PointDto;
import ru.sapronov.common.exception.AccessDeniedException;
import ru.sapronov.order.dao.Courier;
import ru.sapronov.order.dto.*;
import ru.sapronov.order.model.Order;
import ru.sapronov.order.model.Point;
import ru.sapronov.order.service.CourierService;
import ru.sapronov.order.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order/v1.0")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CourierService courierService;
    private final PrincipalProvider principalProvider;

    @PostMapping("/create-order")
    public ResponseEntity<Void> createOrder(@Valid @RequestBody CreateOrderRequestDto request) {
        orderService.createOrder(
                principalProvider.getPrincipal().orElseThrow(AccessDeniedException::new).id(),
                new Point(
                    request.destinationPoint().longitude(),
                    request.destinationPoint().latitude()
                ),
                new Point(
                    request.departurePoint().longitude(),
                    request.departurePoint().latitude()
                ));
        return ResponseEntity.ok(null);
    }

    @PostMapping("/change-order-destination")
    public ResponseEntity<Void> changeOrderDestination(@Valid @RequestBody ChangeOrderDestinationRequestDto request) {
        orderService.changeOrderDestination(
                principalProvider.getPrincipal().orElseThrow(AccessDeniedException::new),
                request.orderId(),
                new Point(
                    request.newDestinationPoint().longitude(),
                    request.newDestinationPoint().latitude()
                )
        );
        return ResponseEntity.ok(null);
    }

    @PostMapping("/cancel-order")
    public ResponseEntity<Void> cancelOrder(@Valid @RequestBody CancelOrderRequestDto request) {
        orderService.cancelByClient(
                principalProvider.getPrincipal().orElseThrow(AccessDeniedException::new),
                request.orderId()
        );
        return ResponseEntity.ok(null);
    }

    @GetMapping("/get-order-details/{orderId}")
    public ResponseEntity<OrderDto> getOrderDetails(@Valid @NotNull @PathVariable UUID orderId) {
        Order order = orderService.getOrder(
                principalProvider.getPrincipal().orElseThrow(AccessDeniedException::new),
                orderId
        );
        // TODO: 10.04.2023 должен быть один вызов одного сервиса
        Courier courier = courierService.findById(order.getCourierId());
        OrderDto orderDto = new OrderDto(
                order.getId(),
                order.getClientId(),
                order.getStatus().name(),
                new CourierDto(
                        courier.getId(),
                        courier.getFirstname(),
                        courier.getLastname()
                ),
                new PointDto(
                        order.getDesctinationPoint().longitude(),
                        order.getDesctinationPoint().latitude()
                ),
                new PointDto(
                        order.getDeparturePoint().longitude(),
                        order.getDeparturePoint().latitude()
                ),
                order.getCreatedAt()
        );
        return ResponseEntity.ok(orderDto);
    }

    @GetMapping("/get-orders")
    public ResponseEntity<List<OrderListItemDto>> getOrders() {
        var dtos = orderService.findAllOrders(principalProvider.getPrincipal()
                        .orElseThrow(AccessDeniedException::new))
                .stream()
                .map(order -> new OrderListItemDto(
                        order.getId(),
                        order.getStatus().name(),
                        order.getFinishedAt()
                ))
                .toList();
         return ResponseEntity.ok(dtos);
    }

    @PostMapping("/assign-courier-to-order")
    public ResponseEntity<Void> assignCourierToOrder(@Valid @RequestBody AssignCourierToOrderRequestDto request) {
        orderService.assignCourierToOrder(
                principalProvider.getPrincipal().orElseThrow(AccessDeniedException::new),
                request.orderId(),
                request.courierId()
        );
        return ResponseEntity.ok(null);
    }

    @PostMapping("/finish-order")
    public ResponseEntity<Void> finishOrder(@Valid @RequestBody FinishOrderRequestDto request) {
        orderService.finishOrder(
                principalProvider.getPrincipal().orElseThrow(AccessDeniedException::new),
                request.orderId()
        );
        return ResponseEntity.ok(null);
    }
}

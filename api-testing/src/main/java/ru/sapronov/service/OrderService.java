package ru.sapronov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import ru.sapronov.client.HttpClient;
import ru.sapronov.order.dto.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class OrderService {

    private final static String ORDER_BASE_URL = "http://localhost:8081/api/order/v1.0";
    private final static String CREATE_ORDER_URL = ORDER_BASE_URL + "/create-order";
    private final static String CHANGE_ORDER_DESTINATION = ORDER_BASE_URL + "/change-order-destination";
    private final static String CANCEL_ORDER_URL = ORDER_BASE_URL + "/cancel-order";
    private final static String GET_ORDER_DETAILS_URL = ORDER_BASE_URL + "/get-order-details";
    private final static String GET_ORDERS_URL = ORDER_BASE_URL + "/get-orders";
    private final static String ASSIGN_COURIER_TO_ORDER_URL = ORDER_BASE_URL + "/assign-courier-to-order";
    private final static String FINISH_ORDER_URL = ORDER_BASE_URL + "/finish-order";
    private final HttpClient httpClient;

    public ResponseEntity<Void> createOrder(CreateOrderRequestDto request, String accessToken) {
        return httpClient.post(CREATE_ORDER_URL, request, Void.class, accessToken);
    }

    public ResponseEntity<Void> changeOrderDestination(ChangeOrderDestinationRequestDto request, String accessToken) {
        return httpClient.post(CHANGE_ORDER_DESTINATION, request, Void.class, accessToken);
    }

    public ResponseEntity<Void> cancelOrder(UUID orderId, String clientAccessToken) {
        return httpClient.post(CANCEL_ORDER_URL, new CancelOrderRequestDto(orderId), Void.class, clientAccessToken);
    }

    public ResponseEntity<OrderDto> getOrderDetails(UUID orderId, String clientAccessToken) {
        return httpClient.get(GET_ORDER_DETAILS_URL + "/" + orderId, OrderDto.class, clientAccessToken);
    }

    public ResponseEntity<List<OrderDto>> getOrders(String accessToken) {
        return httpClient.get(GET_ORDERS_URL, new ParameterizedTypeReference<>(){}, accessToken);
    }

    public ResponseEntity<Void> assignCourierToOrder(UUID orderId, UUID courierId, String adminAccessToken) {
        return httpClient.post(
                ASSIGN_COURIER_TO_ORDER_URL,
                new AssignCourierToOrderRequestDto(
                        orderId, courierId
                ),
                Void.class,
                adminAccessToken
        );
    }

    public ResponseEntity<Void> finishOrder(UUID orderId, String courierAccessToken) {
        return httpClient.post(
                FINISH_ORDER_URL,
                new FinishOrderRequestDto(orderId),
                Void.class,
                courierAccessToken
        );
    }
}

package ru.sapronov.order.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeNames {
    public static final String ORDER_CREATED_EXCHANGE_NAME = "delivery_order-create";
    public static final String ORDER_DESTINATION_CHANGED_EXCHANGE_NAME = "delivery_order-destination-changed";
    public static final String ORDER_CANCELED_EXCHANGE_NAME = "delivery_order-canceled";
    public static final String ORDER_IS_BEING_DELIVERED_EXCHANGE_NAME = "delivery_order-is-being-delivered";
}

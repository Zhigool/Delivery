package ru.sapronov.tracking.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeNames {
    public static final String NO_SUITABLE_COURIER = "delivery_no-suitable-courier";
    public static final String COURIER_FOUND = "delivery_courier-found";
}

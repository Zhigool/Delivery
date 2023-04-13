package ru.sapronov.auth.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeNames {
    public static final String NEW_COURIER_WAS_APPOINTED_EXCHANGE_NAME = "delivery_new-courier-was-appointed";
    public static final String NEW_ACCOUNT_WAS_CREATED_EXCHANGE_NAME = "delivery_new-account-was-created";
}

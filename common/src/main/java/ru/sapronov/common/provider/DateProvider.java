package ru.sapronov.common.provider;

import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class DateProvider {
    public ZonedDateTime now() {
        return ZonedDateTime.now();
    }
}

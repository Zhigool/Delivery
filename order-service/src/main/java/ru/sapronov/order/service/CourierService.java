package ru.sapronov.order.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sapronov.order.dao.Courier;
import ru.sapronov.order.dao.CourierRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourierService {
    private final CourierRepository courierRepository;

    @Transactional
    public void save(Courier courier) {
        courierRepository.save(courier);
    }

    public Courier findById(UUID courierId) {
        return courierRepository.findById(courierId).orElse(null);
    }
}

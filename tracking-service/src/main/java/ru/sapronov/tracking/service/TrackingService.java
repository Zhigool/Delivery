package ru.sapronov.tracking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import ru.sapronov.common.auth.PrincipalProvider;
import ru.sapronov.common.event.EventService;
import ru.sapronov.common.event.order.CourierFoundEvent;
import ru.sapronov.common.event.order.NoSuitableCourierEvent;
import ru.sapronov.common.exception.AccessDeniedException;
import ru.sapronov.common.provider.DateProvider;
import ru.sapronov.tracking.dao.Order;
import ru.sapronov.tracking.dao.TrackingRecord;
import ru.sapronov.tracking.dao.TrackingRecordRepository;
import ru.sapronov.tracking.event.ExchangeNames;
import ru.sapronov.tracking.exception.TrackingInfoNotFound;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final TrackingRecordRepository repository;
    private final OrderService orderService;
    private final DateProvider dateProvider;
    private final EventService eventService;

    private final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

    @Transactional
    public void saveTrackingInfo(UUID courierId, Double longitude, Double latitude) {
        Point point = factory.createPoint(new Coordinate(longitude, latitude));
        Optional<TrackingRecord> optionalRecord = repository.findByCourierId(courierId);
        if (optionalRecord.isPresent()) {
            var trackingRecord = optionalRecord.get();
            trackingRecord.setLocation(point);
            repository.save(trackingRecord);
            return;
        }
        repository.save(new TrackingRecord(
            courierId,
            point,
            dateProvider.now()
        ));
    }

    public void findSuitableCourier(
            UUID orderId,
            Double departureLongitude,
            Double departureLatitude) {
        var optionalRecord = repository.findNearestCourier(departureLongitude, departureLatitude);
        var result = optionalRecord.map(TrackingRecord::getCourierId);
        if (result.isEmpty()) {
            eventService.sendEventToExchange(new NoSuitableCourierEvent(
                    orderId
            ), ExchangeNames.NO_SUITABLE_COURIER);
        } else {
            eventService.sendEventToExchange(new CourierFoundEvent(
                    orderId,
                    result.get()
            ), ExchangeNames.COURIER_FOUND);
        }
    }

    public TrackingRecord getTrackingInfoByOrderId(PrincipalProvider.Principal principal, UUID orderId) {
        Order order = orderService.findById(orderId).orElseThrow(TrackingInfoNotFound::new);
        switch (principal.role()) {
            case CLIENT -> {
                if (!order.getClientId().equals(principal.id())) {
                    throw new AccessDeniedException();
                }
            }
            case COURIER -> {
                if (!order.getCourierId().equals(principal.id())) {
                    throw new AccessDeniedException();
                }
            }
            case ADMIN -> {}
        }
        return repository.findByCourierId(order.getCourierId()).orElseThrow(TrackingInfoNotFound::new);
    }
}

package ru.sapronov.tracking.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface TrackingRecordRepository extends CrudRepository<TrackingRecord, UUID> {
    @Query(value = "SELECT id, courier_id, created_at, location \n" +
            "FROM tracking_record\n" +
            "ORDER BY ST_Distance(location, ST_SetSRID(ST_MakePoint(:departureLongitude, :departureLatitude), 4326)) DESC LIMIT 1;", nativeQuery = true)
    Optional<TrackingRecord> findNearestCourier(Double departureLongitude, Double departureLatitude);

    Optional<TrackingRecord> findByCourierId(UUID courierId);
}

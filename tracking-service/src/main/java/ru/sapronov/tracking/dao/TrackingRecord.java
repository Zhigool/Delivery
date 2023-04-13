package ru.sapronov.tracking.dao;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tracking_record")
@NoArgsConstructor
public class TrackingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID courierId;
    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;
    private ZonedDateTime createdAt;

    public TrackingRecord(UUID courierId, Point point, ZonedDateTime createdAt) {
        this.id = null;
        this.courierId = courierId;
        this.location = point;
        this.createdAt = createdAt;
    }
}

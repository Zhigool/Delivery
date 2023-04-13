package ru.sapronov.tracking.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "delivery_order")
public class Order {
    @Id
    private UUID id;
    private UUID clientId;
    private UUID courierId;
}

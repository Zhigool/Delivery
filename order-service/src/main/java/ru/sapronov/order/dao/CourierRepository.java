package ru.sapronov.order.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CourierRepository extends CrudRepository<Courier, UUID> {}

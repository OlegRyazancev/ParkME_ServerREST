package ru.ryazancev.parkingreservationsystem.services;

import ru.ryazancev.parkingreservationsystem.models.parking.Zone;

import java.util.List;

public interface ZoneService {

    List<Zone> getAll();

    Zone getById(Long zoneId);

    Zone create(Zone zone);

    Zone update(Zone zone);

    void delete(Long zoneId);
}

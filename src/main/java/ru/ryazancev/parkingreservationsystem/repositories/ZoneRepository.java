package ru.ryazancev.parkingreservationsystem.repositories;

import ru.ryazancev.parkingreservationsystem.models.parking.Zone;

import java.util.List;
import java.util.Optional;

public interface ZoneRepository {

    List<Zone> findAll();

    Optional<Zone> findById(Long zoneId);

    void create(Zone zone);

    void update(Zone zone);

    void delete(Long zoneId);

    Optional<Zone> findByNumber(Integer number);
}

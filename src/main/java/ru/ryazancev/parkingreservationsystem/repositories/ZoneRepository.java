package ru.ryazancev.parkingreservationsystem.repositories;

import ru.ryazancev.parkingreservationsystem.models.parking.Zone;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;

public interface ZoneRepository {

    List<Zone> finaAll();

    Optional<Zone> findById(Long zoneId);

    void create(Zone zone);

    void update(Zone zone);

    void delete(Long zoneId);

}

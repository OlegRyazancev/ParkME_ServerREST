package ru.ryazancev.parkingreservationsystem.repositories;

import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;

import java.util.List;
import java.util.Optional;

public interface ZoneRepository {

    List<Zone> findAll();

    Optional<Zone> findById(Long zoneId);

    List<Place> findOccupiedPlacesByZoneId(Long zoneId);

    void create(Zone zone);

    void update(Zone zone);

    void delete(Long zoneId);

    Optional<Zone> findByNumber(Integer number);

    List<Place> findPlacesByZoneNumber(Integer zoneNumber);

    Optional<Place> findPlaceByZoneNumberAndPlaceNumber(Integer zoneNumber, Integer place_number);
}

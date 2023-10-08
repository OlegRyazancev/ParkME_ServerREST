package ru.ryazancev.parkingreservationsystem.repositories;

import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PlaceRepository {

    Optional<Place> findById(Long placeId);

    List<Place> findAllByZoneId(Long zoneId);
    Optional<Place> findByNumberAndZoneNumber(Integer placeNumber, Integer zoneNumber);

    void assignPlaceToZone(Long placeId, Long zoneId);

    void create(Place place);

    void update(Place place);

    void delete(Long placeId);
}

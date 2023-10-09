package ru.ryazancev.parkingreservationsystem.repositories;

import ru.ryazancev.parkingreservationsystem.models.parking.Place;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository {

    Optional<Place> findById(Long placeId);

    List<Place> findAllByZoneId(Long zoneId);

    void assignPlaceToZone(Long placeId, Long zoneId);

    void create(Place place);

    void makeDisable(Place place);

    void delete(Long placeId);
}

package ru.ryazancev.parkingreservationsystem.repositories;

import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository {

    Optional<Place> findById(Long placeId);

    List<Place> findAllByZoneId(Long zoneId);

    List<Place> findAllOccupiedByUserId(Long userId);

    void assignToZoneById(Long placeId, Long zoneId);

    void create(Place place);

    void changeStatus(Place place, Status status);

    void delete(Long placeId);

}

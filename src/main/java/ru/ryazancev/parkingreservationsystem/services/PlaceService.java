package ru.ryazancev.parkingreservationsystem.services;

import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;

import java.util.List;

public interface PlaceService {

    Place getById(Long placeId);

    List<Place> getAllByZoneId(Long zoneId);

    List<Place> getFreePlacesByZoneId(Long zoneId);

    Place create(Place place, Long zoneId);

    Place changeStatus(Long placeId, Status status);

    void delete(Long placeId);
}

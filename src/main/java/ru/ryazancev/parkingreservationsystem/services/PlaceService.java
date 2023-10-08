package ru.ryazancev.parkingreservationsystem.services;

import ru.ryazancev.parkingreservationsystem.models.parking.Place;

import java.util.List;

public interface PlaceService {

    Place getById(Long placeId);

    List<Place> getAllByZoneId(Long zoneId);

    Place create(Place place, Long zoneId);

    Place makeDisable(Place place);

    void delete(Long placeId);
}

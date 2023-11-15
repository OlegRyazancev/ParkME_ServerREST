package ru.ryazancev.parkingreservationsystem.services;

import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;

import java.util.List;

public interface PlaceService {

    Place getById(Long placeId);

    List<Place> createPlacesInZone(Long zoneId, int numberOfPlaces);

    Place changeStatus(Long placeId, Status status);

    Integer countAllPlacesByZoneID(Long zoneId);

    Integer countFreePlacesByZoneID(Long zoneId);

    void delete(Long placeId);


}

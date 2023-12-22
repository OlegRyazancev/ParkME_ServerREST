package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.PlaceStatus;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.services.PlaceService;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;

    @Override
    public Place getById(final Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Place not found"));
    }

    @Transactional
    @Override
    public List<Place> createPlacesInZone(final Long zoneId,
                                          final int numberOfPlaces) {
        if (numberOfPlaces <= 0 || numberOfPlaces > 50) {
            throw new IllegalStateException(
                    "Number of places must be between 1 and 50");
        }
        List<Place> zonesPlaces = placeRepository.findAllByZoneId(zoneId);

        int startNumberOfPlace = getStartNumberOfPlace(zonesPlaces);


        List<Place> createdPlaces = IntStream.range(0, numberOfPlaces)
                .mapToObj(i ->
                        Place.builder()
                                .number(startNumberOfPlace + i)
                                .status(PlaceStatus.FREE)
                                .build())
                .toList();


        createdPlaces = placeRepository.saveAll(createdPlaces);

        createdPlaces.forEach(place ->
                placeRepository.assignToZone(place.getId(), zoneId));
        return createdPlaces;
    }

    @Transactional
    @Override
    public Place changeStatus(final Long placeId,
                              final PlaceStatus placeStatus) {
        if (placeStatus.equals(PlaceStatus.OCCUPIED)) {
            throw new IllegalStateException(
                    "Can not use OCCUPIED status here");
        }
        Place existingPlace = getById(placeId);

        if (existingPlace.getStatus().equals(placeStatus)) {
            throw new IllegalStateException(
                    "Place already has this status");
        }
        if (existingPlace.getStatus().equals(PlaceStatus.OCCUPIED)) {
            throw new IllegalStateException(
                    "Can not change status, because place is occupied");
        }

        existingPlace.setStatus(placeStatus);

        return placeRepository.save(existingPlace);
    }

    @Override
    public Integer countAllPlacesByZoneID(final Long zoneId) {
        return placeRepository.countAllPlacesByZoneId(zoneId);
    }

    @Override
    public Integer countFreePlacesByZoneID(final Long zoneId) {
        return placeRepository.countFreePlacesByZoneId(zoneId);
    }

    @Transactional
    @Override
    public void delete(final Long placeId) {
        Place foundPlace = getById(placeId);
        if (foundPlace.getStatus().equals(PlaceStatus.OCCUPIED)) {
            throw new IllegalStateException("Can not delete occupied place");
        }
        placeRepository.deleteById(foundPlace.getId());
    }

    private int getStartNumberOfPlace(final List<Place> places) {
        return places.isEmpty() ? 1
                : places.stream()
                .max(Comparator.comparingInt(Place::getNumber))
                .map(place -> place.getNumber() + 1)
                .orElse(1);
    }
}

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
        if (numberOfPlaces <= 0) {
            throw new IllegalArgumentException(
                    "Number of places must be a positive integer");
        }
        List<Place> zonesPlaces = placeRepository.findAllByZoneId(zoneId);

        int startNumberOfPlace = getStartNumberOfPlace(zonesPlaces);

        return IntStream.range(0, numberOfPlaces)
                .mapToObj(i ->
                        Place.builder()
                                .number(startNumberOfPlace + i)
                                .placeStatus(PlaceStatus.FREE)
                                .build())
                .peek(placeRepository::save)
                .peek(createdPlace ->
                        placeRepository.assignToZone(
                                createdPlace.getId(),
                                zoneId))
                .toList();
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

        if (existingPlace.getPlaceStatus().equals(placeStatus)) {
            throw new IllegalStateException(
                    "Place already has this status");
        }
        if (existingPlace.getPlaceStatus().equals(PlaceStatus.OCCUPIED)) {
            throw new IllegalStateException(
                    "Can not change status, because place is occupied");
        }

        existingPlace.setPlaceStatus(placeStatus);

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
        if (foundPlace.getPlaceStatus().equals(PlaceStatus.OCCUPIED)) {
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

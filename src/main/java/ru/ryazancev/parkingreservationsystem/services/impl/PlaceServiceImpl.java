package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
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

    @Override
    public List<Place> getAllByZoneId(final Long zoneId) {
        return placeRepository.findAllByZoneId(zoneId);
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
        int startNumberOfPlace = zonesPlaces.isEmpty() ? 1 :
                zonesPlaces.stream()
                        .max(Comparator.comparingInt(Place::getNumber))
                        .map(place -> place.getNumber() + 1)
                        .orElse(1);

        return IntStream.range(0, numberOfPlaces)
                .mapToObj(i ->
                        Place.builder()
                                .number(startNumberOfPlace + i)
                                .status(Status.FREE)
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
                              final Status status) {
        if (status.equals(Status.OCCUPIED)) {
            throw new IllegalStateException(
                    "Can not use OCCUPIED status here");
        }
        Place foundPlace = placeRepository
                .findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Place not found"));
        if (foundPlace.getStatus().equals(status)) {
            throw new IllegalStateException(
                    "Place already has this status");
        }
        if (foundPlace.getStatus().equals(Status.OCCUPIED)) {
            throw new IllegalStateException(
                    "Can not change status, because place is occupied");
        }

        foundPlace.setStatus(status);
        placeRepository.save(foundPlace);
        return foundPlace;
    }

    @Override
    public Integer countAllPlacesByZoneID(Long zoneId) {
        return placeRepository.countAllPlacesByZoneId(zoneId);
    }

    @Override
    public Integer countFreePlacesByZoneID(Long zoneId) {
        return placeRepository.countFreePlacesByZoneId(zoneId);
    }

    @Transactional
    @Override
    public void delete(final Long placeId) {
        Place foundPlace = placeRepository
                .findById(placeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Place not found"));
        if (foundPlace.getStatus().equals(Status.OCCUPIED)) {
            throw new IllegalStateException("Can not delete occupied place");
        }

        placeRepository.deleteById(placeId);
    }
}

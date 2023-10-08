package ru.ryazancev.parkingreservationsystem.repositories.impl;

import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;

import java.util.List;
import java.util.Optional;

public class PlaceRepositoryImpl implements PlaceRepository {
    @Override
    public Optional<Place> findById(Long placeId) {
        return Optional.empty();
    }

    @Override
    public List<Place> findAllByZoneId(Long zoneId) {
        return null;
    }

    @Override
    public Optional<Place> findByNumberAndZoneNumber(Integer placeNumber, Integer zoneNumber) {
        return Optional.empty();
    }

    @Override
    public void assignPlaceToZone(Long placeId, Long zoneId) {

    }

    @Override
    public void create(Place place) {

    }

    @Override
    public void update(Place place) {

    }

    @Override
    public void delete(Long placeId) {

    }
}

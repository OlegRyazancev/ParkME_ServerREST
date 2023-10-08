package ru.ryazancev.parkingreservationsystem.repositories.impl;

import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;

import java.util.List;
import java.util.Optional;

@Repository
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

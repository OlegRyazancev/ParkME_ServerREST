package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.services.PlaceService;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

    private final PlaceRepository placeRepository;

    @Override
    public Place getById(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found"));
    }

    @Override
    public List<Place> getAllByZoneId(Long zoneId) {
        return placeRepository.findAllByZoneId(zoneId);
    }

    @Transactional
    @Override
    public Place create(Place place, Long zoneId) {

        if (placeRepository.findAllByZoneId(zoneId).contains(place))
            throw new IllegalStateException("Place is already exists");

        place.setStatus(Status.FREE);
        placeRepository.create(place);
        placeRepository.assignPlaceToZone(place.getId(), zoneId);

        return place;
    }

    @Transactional
    @Override
    public Place makeDisable(Long placeId) {
        Place foundPlace = placeRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found"));
        if (foundPlace.getStatus().equals(Status.OCCUPIED)||foundPlace.getStatus().equals(Status.DISABLE)) {
            throw new IllegalStateException("Place is already occupied/disable. You can not change status");
        }
        placeRepository.makeDisable(foundPlace);
        foundPlace.setStatus(Status.DISABLE);

        return foundPlace;
    }

    @Transactional
    @Override
    public void delete(Long placeId) {
        placeRepository.delete(placeId);
    }
}

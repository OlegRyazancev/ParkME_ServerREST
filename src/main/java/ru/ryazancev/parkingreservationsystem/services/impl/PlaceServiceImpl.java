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
        if (placeRepository.findAllByZoneId(zoneId)
                .stream()
                .anyMatch(zonePlace -> zonePlace.getNumber().equals(place.getNumber()))) {
            throw new IllegalStateException("Place is already exists in this zone");
        }
        place.setStatus(Status.FREE);
        placeRepository.create(place);
        placeRepository.assignToZoneById(place.getId(), zoneId);

        return place;
    }

    @Transactional
    @Override
    public Place changeStatus(Long placeId, Status status) {
        if (status.equals(Status.OCCUPIED))
            throw new IllegalStateException("Can not use OCCUPIED status here");
        Place foundPlace = placeRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found"));
        if (foundPlace.getStatus().equals(status))
            throw new IllegalStateException("Place already has this status");
        if (foundPlace.getStatus().equals(Status.OCCUPIED))
            throw new IllegalStateException("Can not change status, because place is occupied");
        foundPlace.setStatus(status);
        placeRepository.changeStatus(foundPlace, foundPlace.getStatus());
        return foundPlace;
    }

    @Transactional
    @Override
    public void delete(Long placeId) {
        Place foundPlace = placeRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found"));
        if (foundPlace.getStatus().equals(Status.OCCUPIED))
            throw new IllegalStateException("Can not delete occupied place");
        placeRepository.delete(placeId);
    }
}

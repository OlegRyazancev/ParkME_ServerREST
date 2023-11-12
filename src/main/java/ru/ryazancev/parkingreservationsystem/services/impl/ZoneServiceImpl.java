package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.repositories.ZoneRepository;
import ru.ryazancev.parkingreservationsystem.services.ZoneService;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final PlaceRepository placeRepository;

    @Override
    public List<Zone> getAll() {
        return zoneRepository.findAll();
    }

    @Override
    public Zone getById(final Long zoneId) {
        return zoneRepository.findById(zoneId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Zone not found"));
    }

    @Override
    @Transactional
    public Zone create(final Zone zone) {
        if (zoneRepository.findByNumber(zone.getNumber()).isPresent()) {
            throw new IllegalStateException("Zone is already exists");
        }

        zoneRepository.save(zone);
        return zone;
    }

    @Override
    @Transactional
    public Zone update(final Zone zone) {
        if (zoneRepository.findByNumber(zone.getNumber()).isPresent()) {
            throw new IllegalStateException("Zone is already exists");
        }

        zoneRepository.save(zone);

        return zone;
    }

    @Override
    @Transactional
    public void delete(final Long zoneId) {
        Zone foundZone = zoneRepository
                .findById(zoneId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Zone not found"));

        Hibernate.initialize(foundZone.getPlaces());

        if (foundZone.getPlaces()
                .stream()
                .anyMatch(place -> place.getStatus().equals(Status.OCCUPIED))) {
            throw new IllegalStateException("Zone have occupied places");
        }

        foundZone.getPlaces()
                .forEach(place ->
                        placeRepository.deleteById(place.getId()));

        zoneRepository.deleteById(foundZone.getId());
    }
}

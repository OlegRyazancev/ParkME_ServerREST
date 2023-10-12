package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.repositories.ZoneRepository;
import ru.ryazancev.parkingreservationsystem.services.ZoneService;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Objects;

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
    public Zone getById(Long zoneId) {
        return zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found"));
    }

    @Override
    @Transactional
    public Zone create(Zone zone) {
        if (zoneRepository.findByNumber(zone.getNumber()).isPresent())
            throw new IllegalStateException("Zone is already exists");

        zoneRepository.create(zone);

        return zone;
    }

    @Override
    @Transactional
    public Zone update(Zone zone) {
        if (zoneRepository.findByNumber(zone.getNumber()).isPresent())
            throw new IllegalStateException("Zone is already exists");

        zoneRepository.update(zone);
        zone.setFreePlaces(Objects.requireNonNull(zoneRepository.findById(zone.getId()).orElse(null)).getFreePlaces());

        return zone;
    }

    @Override
    @Transactional
    public void delete(Long zoneId) {
        if (!zoneRepository.findOccupiedPlacesByZoneId(zoneId).isEmpty())
            throw new IllegalStateException("Zone have occupied places");

        placeRepository.findAllByZoneId(zoneId)
                .forEach(place -> placeRepository.delete(place.getId()));
        zoneRepository.delete(zoneId);
    }
}

package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.repositories.ZoneRepository;
import ru.ryazancev.parkingreservationsystem.services.ZoneService;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;

    @Override
    public List<Zone> getAll() {
        return zoneRepository.finaAll();
    }

    @Override
    public Zone getById(Long zoneId) {
        return zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found"));
    }

    @Override
    @Transactional
    public Zone create(Zone zone) {
        zoneRepository.create(zone);

        return zone;
    }

    @Override
    @Transactional
    public Zone update(Zone zone) {
        zoneRepository.update(zone);

        return zone;
    }

    @Override
    @Transactional
    public void delete(Long zoneId) {
        zoneRepository.delete(zoneId);
    }
}

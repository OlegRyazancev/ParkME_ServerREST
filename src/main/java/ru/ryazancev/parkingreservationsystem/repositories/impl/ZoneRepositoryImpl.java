package ru.ryazancev.parkingreservationsystem.repositories.impl;

import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.repositories.ZoneRepository;

import java.util.List;
import java.util.Optional;

public class ZoneRepositoryImpl implements ZoneRepository {
    @Override
    public List<Zone> finaAll() {
        return null;
    }

    @Override
    public Optional<Zone> findById(Long zoneId) {
        return Optional.empty();
    }

    @Override
    public void create(Zone zone) {

    }

    @Override
    public void update(Zone zone) {

    }

    @Override
    public void delete(Long zoneId) {

    }
}

package ru.ryazancev.parkingreservationsystem.repositories.impl;

import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {
    @Override
    public List<Reservation> findAll() {
        return null;
    }

    @Override
    public Optional<Reservation> findById(Long reservationId) {
        return Optional.empty();
    }

    @Override
    public List<Reservation> findAllByUserId(Long userId) {
        return null;
    }

    @Override
    public void assignToUserById(Reservation reservation, Long userId) {

    }

    @Override
    public Reservation update(Reservation reservation) {
        return null;
    }

    @Override
    public void create(Reservation reservation) {

    }

    @Override
    public void delete(Long reservationId) {

    }
}

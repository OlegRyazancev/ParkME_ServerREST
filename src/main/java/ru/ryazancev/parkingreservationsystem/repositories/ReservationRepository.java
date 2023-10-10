package ru.ryazancev.parkingreservationsystem.repositories;

import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    List<Reservation> findAll();

    Optional<Reservation> findById(Long reservationId);

    List<Reservation> findAllByUserId(Long userId);

    void assignToUser(Reservation reservation);

    void update(Reservation reservation);

    void create(Reservation reservation);

    void delete(Long reservationId);

}

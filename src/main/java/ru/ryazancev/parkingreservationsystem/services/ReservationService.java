package ru.ryazancev.parkingreservationsystem.services;

import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;

import java.util.List;

public interface ReservationService {
    List<Reservation> getAll();

    List<Reservation> getReservationsByUserId(Long userId);

    Reservation cancel(Long reservationId);

    Reservation create(Reservation reservation, Long userId);

    Reservation changeTimeTo(Reservation reservation);

    void delete(Long reservationId);

}

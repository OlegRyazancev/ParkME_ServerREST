package ru.ryazancev.parkingreservationsystem.services;

import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;

import java.util.List;

public interface ReservationService {

    List<Reservation> getAll();

    List<Reservation> getExpiredReservations();

    Reservation getInfo(Long reservationId);

    List<Reservation> getReservationsByUserId(Long userId);

    Reservation create(Reservation reservation, Long userId);

    Reservation changeTimeTo(Reservation reservation);

    void delete(Long reservationId);

    void deleteExpiredReservations(List<Reservation> reservations);
}

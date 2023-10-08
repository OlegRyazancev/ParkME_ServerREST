package ru.ryazancev.parkingreservationsystem.services;

import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;

import java.util.List;

public interface ReservationService {

    List<Reservation> getAll();

    Reservation getById(Long reservationId);

    List<Reservation> getAllByUserId(Long userId);

    Reservation create(Reservation reservation, Long id);

    Reservation update(Reservation reservation);

    void delete(Long reservationId);


}

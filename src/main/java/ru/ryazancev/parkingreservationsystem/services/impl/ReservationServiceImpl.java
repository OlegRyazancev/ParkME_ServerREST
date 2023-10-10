package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;
import ru.ryazancev.parkingreservationsystem.services.ReservationService;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    @Override
    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation getInfo(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
    }

    @Override
    public List<Reservation> getAllByUserId(Long userId) {
        List<Reservation> reservations = reservationRepository.findAllByUserId(userId);

        if (reservations.isEmpty())
            throw new IllegalStateException("User don't make any reservation");

        return reservations;
    }

    @Transactional
    @Override
    public Reservation create(Reservation reservation, Long id) {

        if (reservation.getPlace().getStatus().equals(Status.OCCUPIED))
            throw new IllegalStateException("Place is already occupied");

        reservation.getPlace().setStatus(Status.OCCUPIED);
        reservationRepository.create(reservation);
        reservationRepository.assignToUserById(reservation, id);

        return reservation;
    }

    @Transactional
    @Override
    public Reservation extend(Reservation reservation) {
        if (reservation.getTimeFrom().isBefore(reservation.getTimeTo())) {
            throw new IllegalStateException("Can not extend reservation, because time from is before time to");
        }
        reservationRepository.extend(reservation);
        return reservation;
    }

    @Transactional
    @Override
    public void delete(Long reservationId) {
        reservationRepository.delete(reservationId);
    }
}

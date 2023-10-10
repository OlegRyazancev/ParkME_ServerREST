package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;
import ru.ryazancev.parkingreservationsystem.services.ReservationService;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final PlaceRepository placeRepository;

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
    public List<Reservation> getReservationsByUserId(Long userId) {
        List<Reservation> reservations = reservationRepository.findAllByUserId(userId);

        if (reservations.isEmpty())
            throw new IllegalStateException("User don't make any reservation");

        return reservations;
    }

    @Override
    public Reservation create(Reservation reservation) {

        if (reservation.getPlace().getStatus().equals(Status.OCCUPIED) || reservation.getPlace().getStatus().equals(Status.DISABLE))
            throw new IllegalStateException("Place is already occupied or disable");
        if (carRepository.findReservationByCarId(reservation.getCar().getId()).isPresent()) {
            throw new IllegalStateException("Car already has reservation");
        }
        reservation.getPlace().setStatus(Status.OCCUPIED);
        placeRepository.changeStatus(reservation.getPlace());
        reservationRepository.create(reservation);
        reservationRepository.assignToUser(reservation);

        return reservation;
    }

    @Transactional
    @Override
    public Reservation changeTimeTo(Reservation reservation) {

        Reservation foundReservation = reservationRepository
                .findById(reservation.getId()).orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (foundReservation.getTimeFrom().isAfter(reservation.getTimeTo())) {
            throw new IllegalStateException("Can not extend reservation, because time from is before time to");
        }

        foundReservation.setTimeTo(reservation.getTimeTo());
        reservationRepository.update(foundReservation);

        return foundReservation;
    }

    @Transactional
    @Override
    public void delete(Long reservationId) {
        Reservation foundReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalStateException("Reservation not found"));

        reservationRepository.delete(foundReservation.getId());
        foundReservation.getPlace().setStatus(Status.FREE);
        placeRepository.changeStatus(foundReservation.getPlace());
    }
}

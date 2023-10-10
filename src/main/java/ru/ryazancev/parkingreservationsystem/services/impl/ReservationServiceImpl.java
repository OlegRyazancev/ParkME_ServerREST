package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;
import ru.ryazancev.parkingreservationsystem.repositories.ZoneRepository;
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
    private final ZoneRepository zoneRepository;

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

    @Transactional
    @Override
    public Reservation create(Reservation reservation) {
        Zone zone = reservation.getZone();
        Place place = reservation.getPlace();

        Place foundPlace = zoneRepository.findPlaceByZoneNumberAndPlaceNumber(zone.getNumber(), place.getNumber())
                .orElseThrow(() -> new IllegalStateException("In the selected zone, there is no place with such a number"));

        Car foundCar = carRepository.findByNumber(reservation.getCar().getNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        if (foundPlace.getStatus() != Status.FREE) {
            throw new IllegalStateException("Place is already occupied or disabled");
        }


        if (carRepository.existsReservationByCarNumber(foundCar.getNumber())) {
            throw new IllegalStateException("Car already has a reservation");
        }

        foundPlace.setStatus(Status.OCCUPIED);
        reservation.setCar(foundCar);
        reservation.setPlace(foundPlace);
        placeRepository.changeStatus(foundPlace, foundPlace.getStatus());

        reservationRepository.create(reservation);
        reservationRepository.assignToUser(reservation);

        return reservation;
    }

    @Transactional
    @Override
    public Reservation changeTimeTo(Reservation reservation) {

        Reservation foundReservation = reservationRepository.findById(reservation.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

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
        placeRepository.changeStatus(foundReservation.getPlace(), Status.FREE);
    }
}

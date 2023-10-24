package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.repositories.*;
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
    private final UserRepository userRepository;

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
    public Reservation create(Reservation reservation, Long user_id) {

        Zone foundZone = zoneRepository.findByNumber(reservation.getZone().getNumber())
                .orElseThrow(() -> new ResourceNotFoundException("No zone with the specified number"));

        Place foundPlace = foundZone.getPlaces().stream()
                .filter(place -> place.getNumber().equals(reservation.getPlace().getNumber()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No place with the specified number in the selected zone"));

        if (foundPlace.getStatus() != Status.FREE)
            throw new IllegalStateException("Place is already occupied or disabled");

        Car foundCar = carRepository.findByNumber(reservation.getCar().getNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        if (carRepository.findReservationByCarId(foundCar.getId()).isPresent())
            throw new IllegalStateException("Car already has a reservation");

        User foundUser = userRepository.findById(user_id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));


        foundPlace.setStatus(Status.OCCUPIED);
        placeRepository.save(foundPlace);

        reservation.setZone(foundZone);
        reservation.setCar(foundCar);
        reservation.setPlace(foundPlace);
        reservation.setUser(foundUser);

        reservationRepository.save(reservation);

        return reservation;
    }

    @Transactional
    @Override
    public Reservation changeTimeTo(Reservation reservation) {

        Reservation foundReservation = reservationRepository.findById(reservation.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (foundReservation.getTimeFrom().isAfter(reservation.getTimeTo()))
            throw new IllegalStateException("Can not extend reservation, because time from is before time to");

        foundReservation.setTimeTo(reservation.getTimeTo());
        reservationRepository.save(foundReservation);

        return foundReservation;
    }

    @Transactional
    @Override
    public void delete(Long reservationId) {

        Reservation foundReservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        foundReservation.getPlace().setStatus(Status.FREE);
        placeRepository.save(foundReservation.getPlace());
        reservationRepository.deleteById(foundReservation.getId());
    }


}

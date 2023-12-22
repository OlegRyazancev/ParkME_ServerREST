package ru.ryazancev.parkingreservationsystem.util;

import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.reservation.ReservationStatus;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;

import java.util.List;

public class ReservationUtils {

    public static boolean isOverlap(final Reservation res1,
                                    final Reservation res2) {
        return !(res1.getTimeFrom().isAfter(res2.getTimeTo())
                || res1.getTimeTo().isBefore(res2.getTimeFrom()));
    }

    public static boolean checkIntervalOverlap(final List<Reservation> existing,
                                               final Reservation actual) {
        return existing.stream()
                .anyMatch(res -> isOverlap(res, actual));
    }


    public static List<Reservation> findActiveOrPlannedReservationsByPlace(
            final Place place,
            final ReservationRepository reservationRepository) {
        return reservationRepository.findAllByPlaceId(place.getId())
                .stream()
                .filter(res ->
                        res.getStatus()
                                .equals(ReservationStatus.ACTIVE)
                                || res.getStatus()
                                .equals(ReservationStatus.PLANNED))
                .toList();
    }

    public static List<Reservation> findActiveOrPlannedResByCar(
            final Car car,
            final ReservationRepository reservationRepository) {
        return reservationRepository.findAllByCarId(car.getId())
                .stream()
                .filter(res ->
                        res.getStatus()
                                .equals(ReservationStatus.ACTIVE)
                                || res.getStatus()
                                .equals(ReservationStatus.PLANNED))
                .toList();
    }

    public static void validateNoOverlap(final List<Reservation> reservations,
                                   final Reservation newReservation,
                                   final String errorMessage) {
        if (checkIntervalOverlap(reservations, newReservation)) {
            throw new IllegalStateException(errorMessage);
        }
    }
}

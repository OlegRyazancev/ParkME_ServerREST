package ru.ryazancev.parkingreservationsystem.repositories;

import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;

import java.util.List;
import java.util.Optional;

public interface CarRepository {
    List<Car> findAll();

    List<Car> findAllByUserId(Long userId);

    Optional<Car> findById(Long carId);

    void create(Car car);

    void update(Car car);

    void assignToUserById(Long carId, Long userId);

    void delete(Long carId);

    Optional<Car> findByNumber(String number);

    Optional<Reservation> findReservationByCarId(Long carId);

    Optional<Reservation> findReservationByCarNumber(String carNumber);

    boolean existsReservationByCarNumber(String number);
}

package ru.ryazancev.parkingreservationsystem.repositories;

import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;

import java.util.List;
import java.util.Optional;

public interface CarRepository {

    List<Car> findAll();

    Optional<Car> findById(Long carId);

    Optional<Car> findByNumber(String number);

    List<Car> findAllByUserId(Long userId);

    Optional<Reservation> findReservationByCarId(Long carId);

    boolean existsReservationByCarNumber(String number);

    void assignToUserById(Long carId, Long userId);

    void create(Car car);

    void update(Car car);

    void delete(Long carId);
}

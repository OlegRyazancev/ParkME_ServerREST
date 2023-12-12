package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.reservation.ReservationStatus;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;
import ru.ryazancev.parkingreservationsystem.services.CarService;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public List<Car> getAll() {
        return carRepository.findAll();
    }

    @Override
    public Car getById(final Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Car not found"));
    }

    @Override
    public List<Car> getAllByUserId(final Long userId) {
        List<Car> cars = carRepository.findAllByUserId(userId);
        if (cars.isEmpty()) {
            throw new IllegalStateException("User hasn't registered any car");
        }
        return cars;
    }

    @Transactional
    @Override
    public Car create(final Car car, final Long userId) {
        if (carRepository.findByNumber(car.getNumber()).isPresent()) {
            throw new IllegalStateException("Car with this "
                    + "number already exists");
        }
        carRepository.save(car);
        carRepository.assignToUser(userId, car.getId());
        return car;
    }

    @Transactional
    @Override
    public Car update(final Car car) {
        if (carRepository.findByNumber(car.getNumber()).isPresent()) {
            throw new IllegalStateException("Car with this number "
                    + "already exists");
        }
        Car existingCar = getById(car.getId());
        existingCar.setNumber(car.getNumber());
        return carRepository.save(existingCar);
    }

    @Transactional
    @Override
    public void delete(final Long carId) {
        List<Reservation> carReservations = reservationRepository
                .findAllByCarId(carId);

        if (carReservations.stream()
                .anyMatch(res ->
                        res.getStatus()
                                .equals(ReservationStatus.ACTIVE)
                                || res.getStatus()
                                .equals(ReservationStatus.PLANNED))) {
            throw new IllegalStateException("Can not delete!"
                    + " Car has active or planned reservations.");
        }

        carRepository.deleteById(carId);
    }
}

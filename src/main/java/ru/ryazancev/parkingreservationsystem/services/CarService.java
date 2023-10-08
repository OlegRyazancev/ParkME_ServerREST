package ru.ryazancev.parkingreservationsystem.services;

import ru.ryazancev.parkingreservationsystem.models.car.Car;

import java.util.List;

public interface CarService {

    List<Car> getAll();

    List<Car> getAllByUserId(Long userId);

    Car getById(Long carId);

    Car create(Car car, Long userId);

    Car update(Car car);

    void delete(Long carId);
}

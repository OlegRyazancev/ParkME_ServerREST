package ru.ryazancev.parkingreservationsystem.repositories.impl;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class CarRepositoryImpl implements CarRepository {
    @Override
    public List<Car> findAll() {
        return null;
    }

    @Override
    public List<Car> findAllByUserId(Long userId) {
        return null;
    }

    @Override
    public Optional<Car> findById(Long carId) {
        return Optional.empty();
    }

    @Override
    public void create(Car car) {

    }

    @Override
    public void update(Car car) {

    }

    @Override
    public void assignCarToUserById(Long carId, Long userId) {

    }

    @Override
    public void delete(Long carId) {

    }

    @Override
    public Optional<Car> findByNumber(String number) {
        return Optional.empty();
    }
}

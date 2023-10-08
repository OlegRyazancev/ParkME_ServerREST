package ru.ryazancev.parkingreservationsystem.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.repositories.DataSourceConfig;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceMappingException;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CarRepositoryImpl implements CarRepository {

    private final DataSourceConfig dataSourceConfig;

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

package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.services.CarService;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Override
    public List<Car> getAll() {
        return carRepository.findAll();
    }

    @Override
    public List<Car> getAllByUserId(Long userId) {
        List<Car> cars = carRepository.findAllByUserId(userId);

        if (cars.isEmpty())
            throw new IllegalStateException("User don't register any car");

        return cars;
    }

    @Override
    public Car getById(Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
    }

    @Transactional
    @Override
    public Car create(Car car, Long userId) {
        if (carRepository.findByNumber(car.getNumber()).isPresent())
            throw new IllegalStateException("Car already exists");

        carRepository.create(car);
        carRepository.assignCarToUserById(car.getId(), userId);

        return car;
    }

    @Transactional
    @Override
    public Car update(Car car) {
        if (carRepository.findByNumber(car.getNumber()).isPresent())
            throw new IllegalStateException("Car already exists");

        carRepository.update(car);

        return car;
    }

    @Transactional
    @Override
    public void delete(Long carId) {
        carRepository.delete(carId);
    }
}

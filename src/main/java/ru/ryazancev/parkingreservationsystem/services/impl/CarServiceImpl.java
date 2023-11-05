package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
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
    public Car getById(Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
    }

    @Override
    public List<Car> getAllByUserId(Long userId) {
        List<Car> cars = carRepository.findAllByUserId(userId);
        if (cars.isEmpty())
            throw new IllegalStateException("User hasn't registered any car");
        return cars;
    }

    @Transactional
    @Override
    public Car create(final Car car, Long userId) {
        if (carRepository.findByNumber(car.getNumber()).isPresent())
            throw new IllegalStateException("Car already exists");
        carRepository.save(car);
        carRepository.assignToUser(userId, car.getId());
        return car;
    }

    @Transactional
    @Override
    public Car update(Car car) {
        if (carRepository.findByNumber(car.getNumber()).isPresent())
            throw new IllegalStateException("Car has the same number");
        if (carRepository.findById(car.getId()).isEmpty())
            throw new ResourceNotFoundException("Car does not exists");
        carRepository.save(car);
        return car;
    }

    @Transactional
    @Override
    public void delete(Long carId) {
        if (reservationRepository.findByCarId(carId).isPresent())
            throw new IllegalStateException("Can not delete car, because car has reservations");

        carRepository.deleteById(carId);
    }
}

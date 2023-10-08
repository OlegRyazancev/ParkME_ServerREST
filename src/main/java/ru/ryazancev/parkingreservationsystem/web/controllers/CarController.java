package ru.ryazancev.parkingreservationsystem.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.services.CarService;
import ru.ryazancev.parkingreservationsystem.web.dto.CarDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.validation.OnUpdate;
import ru.ryazancev.parkingreservationsystem.web.mappers.CarMapper;

import java.util.List;


@RestController
@RequestMapping("/api/vi/cars")
@RequiredArgsConstructor
@Validated
public class CarController {

    private final CarService carService;
    private final CarMapper carMapper;

    @GetMapping
    public List<CarDTO> getCars() {
        List<Car> cars = carService.getAll();

        return carMapper.toDTO(cars);
    }

    @GetMapping("/{id}")
    public CarDTO getById(@PathVariable("id") Long id) {
        Car car = carService.getById(id);

        return carMapper.toDTO(car);
    }

    @PutMapping
    public CarDTO update(@Validated(OnUpdate.class) @RequestBody CarDTO carDTO) {
        Car car = carMapper.toEntity(carDTO);
        Car updatedCar = carService.update(car);

        return carMapper.toDTO(updatedCar);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        carService.delete(id);
    }


}

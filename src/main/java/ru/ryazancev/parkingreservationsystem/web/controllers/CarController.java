package ru.ryazancev.parkingreservationsystem.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.services.CarService;
import ru.ryazancev.parkingreservationsystem.util.mappers.car.CarMapper;
import ru.ryazancev.parkingreservationsystem.util.validation.OnUpdate;
import ru.ryazancev.parkingreservationsystem.web.dto.car.CarDTO;


@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
@Validated
@Tag(name = "Car Controller", description = "Car API")
public class CarController {

    private final CarService carService;
    private final CarMapper carMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Get car by id")
    @PreAuthorize("@customSecurityExpression.canAccessCar(#id)")
    public CarDTO getById(@PathVariable("id") Long id) {
        Car car = carService.getById(id);

        return carMapper.toDTO(car);
    }

    @PutMapping
    @Operation(summary = "Update car")
    @PreAuthorize("@customSecurityExpression.canAccessCar(#carDTO.id)")
    public CarDTO update(@Validated(OnUpdate.class) @RequestBody CarDTO carDTO) {
        Car car = carMapper.toEntity(carDTO);
        Car updatedCar = carService.update(car);

        return carMapper.toDTO(updatedCar);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete car by id")
    @PreAuthorize("@customSecurityExpression.canAccessCar(#id)")
    public void deleteById(@PathVariable("id") Long id) {
        carService.delete(id);
    }
}

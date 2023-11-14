package ru.ryazancev.parkingreservationsystem.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.services.CarService;
import ru.ryazancev.parkingreservationsystem.util.mappers.CarMapper;
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
    @QueryMapping("carById")
    @Operation(summary = "Get car by id")
    @PreAuthorize("@customSecurityExpression.canAccessCar(#carId)")
    public CarDTO getById(
            @PathVariable("id")
            @Argument final Long carId) {
        Car car = carService.getById(carId);

        return carMapper.toDTO(car);
    }

    @PutMapping
    @MutationMapping("updateCar")
    @Operation(summary = "Update car")
    @PreAuthorize("@customSecurityExpression.canAccessCar(#carDTO.id)")
    public CarDTO update(
            @Validated(OnUpdate.class)
            @RequestBody
            @Argument final CarDTO carDTO) {
        Car car = carMapper.toEntity(carDTO);
        Car updatedCar = carService.update(car);

        return carMapper.toDTO(updatedCar);
    }

    @DeleteMapping("/{id}")
    @MutationMapping("deleteCar")
    @Operation(summary = "Delete car by id")
    @PreAuthorize("@customSecurityExpression.canAccessCar(#carId)")
    public void deleteById(
            @PathVariable("id")
            @Argument final Long carId) {
        carService.delete(carId);
    }
}

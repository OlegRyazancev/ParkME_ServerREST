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
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.services.CarService;
import ru.ryazancev.parkingreservationsystem.services.ReservationService;
import ru.ryazancev.parkingreservationsystem.services.UserService;
import ru.ryazancev.parkingreservationsystem.util.mappers.CarMapper;
import ru.ryazancev.parkingreservationsystem.util.mappers.ReservationInfoMapper;
import ru.ryazancev.parkingreservationsystem.util.mappers.ReservationMapper;
import ru.ryazancev.parkingreservationsystem.util.mappers.UserMapper;
import ru.ryazancev.parkingreservationsystem.util.validation.OnCreate;
import ru.ryazancev.parkingreservationsystem.util.validation.OnUpdate;
import ru.ryazancev.parkingreservationsystem.web.dto.car.CarDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationInfoDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.user.UserDTO;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Controller", description = "User API")
public class UserController {

    private final UserService userService;
    private final CarService carService;
    private final ReservationService reservationService;

    private final UserMapper userMapper;
    private final CarMapper carMapper;
    private final ReservationMapper reservationMapper;
    private final ReservationInfoMapper reservationInfoMapper;

    @GetMapping("/{id}")
    @QueryMapping("userById")
    @Operation(summary = "Get user by id")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public UserDTO getById(@PathVariable("id") @Argument Long id) {
        User user = userService.getById(id);

        return userMapper.toDTO(user);
    }

    @GetMapping("/{id}/cars")
    @QueryMapping("carsByUserId")
    @Operation(summary = "Get cars by user id")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public List<CarDTO> getCarsByUserId(@PathVariable("id") @Argument Long id) {
        List<Car> cars = carService.getAllByUserId(id);

        return carMapper.toDTO(cars);
    }

    @GetMapping("/{id}/reservations")
    @QueryMapping("reservationsByUserId")
    @Operation(summary = "Get reservations by user id")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public List<ReservationDTO> getReservationsByUserId(@PathVariable("id") @Argument Long id) {
        List<Reservation> reservations = reservationService.getReservationsByUserId(id);

        return reservationMapper.toDTO(reservations);
    }


    @PostMapping("/{id}/cars")
    @MutationMapping("createCar")
    @Operation(summary = "Create car")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public CarDTO createCar(@PathVariable("id")
                            @Argument Long id,
                            @Validated(OnCreate.class)
                            @RequestBody
                            @Argument CarDTO carDTO) {
        Car car = carMapper.toEntity(carDTO);
        Car createdCar = carService.create(car, id);

        return carMapper.toDTO(createdCar);
    }

    @PostMapping("/{id}/reservations")
    @MutationMapping("makeReservation")
    @Operation(summary = "Make reservation")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public ReservationDTO makeReservation(@PathVariable("id")
                                          @Argument Long id,
                                          @Validated(OnCreate.class)
                                          @RequestBody
                                          @Argument ReservationInfoDTO reservationInfoDTO) {
        Reservation reservation = reservationInfoMapper.toEntity(reservationInfoDTO);
        Reservation createdReservation = reservationService.create(reservation, id);

        return reservationMapper.toDTO(createdReservation);
    }


    @PutMapping
    @MutationMapping("updateUser")
    @Operation(summary = "Update user")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#userDTO.id)")
    public UserDTO update(@Validated(OnUpdate.class)
                          @RequestBody
                          @Argument UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User updatedUser = userService.update(user);

        return userMapper.toDTO(updatedUser);
    }


    @DeleteMapping("/{id}")
    @MutationMapping("deleteUser")
    @Operation(summary = "Delete user by id")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public void deleteById(@PathVariable("id")
                           @Argument Long id) {
        userService.delete(id);
    }
}

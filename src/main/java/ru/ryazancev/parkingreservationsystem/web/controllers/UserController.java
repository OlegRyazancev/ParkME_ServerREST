package ru.ryazancev.parkingreservationsystem.web.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.services.CarService;
import ru.ryazancev.parkingreservationsystem.services.ReservationService;
import ru.ryazancev.parkingreservationsystem.services.UserService;
import ru.ryazancev.parkingreservationsystem.web.dto.car.CarDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationInfoDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.user.UserDTO;
import ru.ryazancev.parkingreservationsystem.util.validation.OnCreate;
import ru.ryazancev.parkingreservationsystem.util.validation.OnUpdate;
import ru.ryazancev.parkingreservationsystem.util.mappers.car.CarMapper;
import ru.ryazancev.parkingreservationsystem.util.mappers.reservation.ReservationInfoMapper;
import ru.ryazancev.parkingreservationsystem.util.mappers.reservation.ReservationMapper;
import ru.ryazancev.parkingreservationsystem.util.mappers.user.UserMapper;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final CarService carService;
    private final ReservationService reservationService;

    private final UserMapper userMapper;
    private final CarMapper carMapper;
    private final ReservationMapper reservationMapper;
    private final ReservationInfoMapper reservationInfoMapper;

    @GetMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public UserDTO getById(@PathVariable("id") Long id) {
        User user = userService.getById(id);

        return userMapper.toDTO(user);
    }

    @GetMapping("/{id}/cars")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public List<CarDTO> getCarsByUserId(@PathVariable("id") Long id) {
        List<Car> cars = carService.getAllByUserId(id);

        return carMapper.toDTO(cars);
    }

    @GetMapping("/{id}/reservations")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public List<ReservationDTO> getReservationsByUserId(@PathVariable("id") Long id) {
        List<Reservation> reservations = reservationService.getReservationsByUserId(id);

        return reservationMapper.toDTO(reservations);
    }


    @PostMapping("/{id}/cars")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public CarDTO createCar(@PathVariable("id") Long id, @Validated(OnCreate.class) @RequestBody CarDTO carDTO) {
        Car car = carMapper.toEntity(carDTO);
        Car createdCar = carService.create(car, id);

        return carMapper.toDTO(createdCar);
    }

    @PostMapping("/reservations")
    @PreAuthorize("@customSecurityExpression.canAccessCar(#reservationInfoDTO.car.id)")
    public ReservationDTO makeReservation( @Validated(OnCreate.class) @RequestBody ReservationInfoDTO reservationInfoDTO) {
        Reservation reservation = reservationInfoMapper.toEntity(reservationInfoDTO);
        Reservation createdReservation = reservationService.create(reservation);

        return reservationMapper.toDTO(createdReservation);
    }


    @PutMapping
    @PreAuthorize("@customSecurityExpression.canAccessUser(#userDTO.id)")
    public UserDTO update(@Validated(OnUpdate.class) @RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User updatedUser = userService.update(user);

        return userMapper.toDTO(updatedUser);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public void deleteById(@PathVariable("id") Long id) {
        userService.delete(id);
    }
}

package ru.ryazancev.parkingreservationsystem.web.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.services.CarService;
import ru.ryazancev.parkingreservationsystem.services.ReservationService;
import ru.ryazancev.parkingreservationsystem.services.UserService;
import ru.ryazancev.parkingreservationsystem.web.dto.CarDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.ReservationDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.UserDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.validation.OnCreate;
import ru.ryazancev.parkingreservationsystem.web.dto.validation.OnUpdate;
import ru.ryazancev.parkingreservationsystem.web.mappers.CarMapper;
import ru.ryazancev.parkingreservationsystem.web.mappers.ReservationMapper;
import ru.ryazancev.parkingreservationsystem.web.mappers.UserMapper;

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


    @GetMapping
    public List<UserDTO> getUsers() {
        List<User> users = userService.getAll();

        return userMapper.toDTO(users);
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable("id") Long id) {
        User user = userService.getById(id);

        return userMapper.toDTO(user);
    }

    @PutMapping
    public UserDTO update(@Validated(OnUpdate.class) @RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User updatedUser = userService.update(user);

        return userMapper.toDTO(updatedUser);
    }

    @GetMapping("/{id}/cars")
    public List<CarDTO> getCarsByUserId(@PathVariable("id") Long id) {
        List<Car> cars = carService.getAllByUserId(id);

        return carMapper.toDTO(cars);
    }

    @PostMapping("/{id}/cars")
    public CarDTO createCar(@PathVariable("id") Long id, @Validated(OnCreate.class) @RequestBody CarDTO carDTO) {
        Car car = carMapper.toEntity(carDTO);
        Car createdCar = carService.create(car, id);

        return carMapper.toDTO(createdCar);
    }

    @GetMapping("/{id}/reservations")
    public List<ReservationDTO> getReservationsByUserId(@PathVariable("id") Long id) {
        List<Reservation> reservations = reservationService.getAllByUserId(id);

        return reservationMapper.toDTO(reservations);
    }

    @PostMapping("/{id}/reservations")
    public ReservationDTO makeReservation(@PathVariable("id") Long id, @Validated(OnCreate.class) @RequestBody ReservationDTO reservationDTO) {
        Reservation reservation = reservationMapper.toEntity(reservationDTO);
        Reservation createdReservation = reservationService.create(reservation, id);

        return reservationMapper.toDTO(createdReservation);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        userService.delete(id);
    }


}

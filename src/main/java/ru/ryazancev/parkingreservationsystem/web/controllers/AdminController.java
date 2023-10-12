package ru.ryazancev.parkingreservationsystem.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.services.*;
import ru.ryazancev.parkingreservationsystem.util.mappers.car.CarMapper;
import ru.ryazancev.parkingreservationsystem.util.mappers.place.PlaceMapper;
import ru.ryazancev.parkingreservationsystem.util.mappers.reservation.ReservationInfoMapper;
import ru.ryazancev.parkingreservationsystem.util.mappers.reservation.ReservationMapper;
import ru.ryazancev.parkingreservationsystem.util.mappers.user.UserMapper;
import ru.ryazancev.parkingreservationsystem.util.mappers.zone.ZoneMapper;
import ru.ryazancev.parkingreservationsystem.util.validation.OnCreate;
import ru.ryazancev.parkingreservationsystem.util.validation.OnUpdate;
import ru.ryazancev.parkingreservationsystem.web.dto.car.CarDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.place.PlaceDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationInfoDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.user.UserDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.zone.ZoneDTO;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class AdminController {

    private final ZoneService zoneService;
    private final PlaceService placeService;
    private final UserService userService;
    private final ReservationService reservationService;
    private final CarService carService;

    private final ZoneMapper zoneMapper;
    private final PlaceMapper placeMapper;
    private final UserMapper userMapper;
    private final ReservationMapper reservationMapper;
    private final ReservationInfoMapper reservationInfoMapper;
    private final CarMapper carMapper;

    @GetMapping("/cars")
    public List<CarDTO> getCars() {
        List<Car> cars = carService.getAll();

        return carMapper.toDTO(cars);
    }

    @GetMapping("/users")
    public List<UserDTO> getUsers() {
        List<User> users = userService.getAll();

        return userMapper.toDTO(users);
    }

    @GetMapping("/reservations")
    public List<ReservationDTO> getReservations() {
        List<Reservation> reservations = reservationService.getAll();
        return reservationMapper.toDTO(reservations);
    }

    @GetMapping("/reservations/{id}")
    public ReservationInfoDTO getReservationInfoById(@PathVariable("id") Long id) {
        Reservation reservation = reservationService.getInfo(id);

        return reservationInfoMapper.toDTO(reservation);
    }

    @GetMapping("/places/{id}")
    public PlaceDTO getPlaceById(@PathVariable("id") Long id) {
        Place place = placeService.getById(id);
        return placeMapper.toDTO(place);
    }

    @PostMapping("/zones")
    public ZoneDTO createZone(@Validated(OnCreate.class) @RequestBody ZoneDTO zoneDTO) {
        Zone zone = zoneMapper.toEntity(zoneDTO);
        Zone createdZone = zoneService.create(zone);

        return zoneMapper.toDTO(createdZone);
    }

    @PostMapping("/zones/{id}/places")
    public PlaceDTO createPlaceInZoneById(@PathVariable("id") Long zoneId, @Validated(OnCreate.class) @RequestBody PlaceDTO placeDTO) {
        Place place = placeMapper.toEntity(placeDTO);
        Place createdPlace = placeService.create(place, zoneId);

        return placeMapper.toDTO(createdPlace);
    }

    @PutMapping("/zones")
    public ZoneDTO updateZone(@Validated(OnUpdate.class) @RequestBody ZoneDTO zoneDTO) {
        Zone zone = zoneMapper.toEntity(zoneDTO);
        Zone updatedZone = zoneService.update(zone);

        return zoneMapper.toDTO(updatedZone);
    }

    @PutMapping("places/{id}/status")
    public PlaceDTO changePlaceStatusById(@PathVariable("id") Long id, @RequestParam String status) {

        Place disabledPlace = placeService.changeStatus(id, Status.valueOf(status));

        return placeMapper.toDTO(disabledPlace);
    }

    @DeleteMapping("zones/{id}")
    public void deleteZoneAndAssociatedPlaces(@PathVariable("id") Long id) {
        zoneService.delete(id);
    }

    @DeleteMapping("places/{id}")
    public void deletePlaceById(@PathVariable("id") Long id) {
        placeService.delete(id);
    }

}

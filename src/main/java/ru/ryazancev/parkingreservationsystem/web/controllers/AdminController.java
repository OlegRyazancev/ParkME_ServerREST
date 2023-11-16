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
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.services.*;
import ru.ryazancev.parkingreservationsystem.util.mappers.*;
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
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Controller", description = "Admin API")
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
    @QueryMapping("cars")
    @Operation(summary = "Get all cars")
    public List<CarDTO> getCars() {
        List<Car> cars = carService.getAll();

        return carMapper.toDTO(cars);
    }

    @GetMapping("/users")
    @QueryMapping("users")
    @Operation(summary = "Get all users")
    public List<UserDTO> getUsers() {
        List<User> users = userService.getAll();

        return userMapper.toDTO(users);
    }

    @GetMapping("/reservations")
    @QueryMapping("reservations")
    @Operation(summary = "Get all reservations")
    public List<ReservationDTO> getReservations() {
        List<Reservation> reservations = reservationService.getAll();
        return reservationMapper.toDTO(reservations);
    }

    @GetMapping("/reservations/{id}")
    @QueryMapping("reservationInfoById")
    @Operation(summary = "Get reservation info by id")
    public ReservationInfoDTO getReservationInfoById(
            @PathVariable("id")
            @Argument final Long reservationId) {
        Reservation reservation = reservationService.getInfo(reservationId);

        return reservationInfoMapper.toDTO(reservation);
    }

    @GetMapping("/places/{id}")
    @QueryMapping("placeById")
    @Operation(summary = "Get place by id")
    public PlaceDTO getPlaceById(
            @PathVariable("id")
            @Argument final Long placeId) {
        Place place = placeService.getById(placeId);
        return placeMapper.toDTO(place);
    }

    @PostMapping("/zones")
    @MutationMapping("createZone")
    @Operation(summary = "Create zone")
    public ZoneDTO createZone(
            @Validated(OnCreate.class)
            @RequestBody
            @Argument final ZoneDTO zoneDTO) {
        Zone zone = zoneMapper.toEntity(zoneDTO);
        Zone createdZone = zoneService.create(zone);

        return zoneMapper.toDTO(createdZone);
    }

    @PostMapping("/zones/{id}/places")
    @MutationMapping("createPlacesInZone")
    @Operation(summary = "Create a fix number places in zone")
    public List<PlaceDTO> createPlacesInZone(
            @PathVariable("id")
            @Argument final Long zoneId,
            @RequestParam("places") final int numberOfPlaces) {

        List<Place> createdPlaces = placeService
                .createPlacesInZone(zoneId, numberOfPlaces);

        return placeMapper.toDTO(createdPlaces);
    }

    @PutMapping("/zones")
    @MutationMapping("updateZone")
    @Operation(summary = "Update zone")
    public ZoneDTO updateZone(
            @Validated(OnUpdate.class)
            @RequestBody
            @Argument final ZoneDTO zoneDTO) {
        Zone zone = zoneMapper.toEntity(zoneDTO);
        Zone updatedZone = zoneService.update(zone);
        ZoneDTO updatedZoneDTO = zoneMapper.toDTO(updatedZone);
        updatedZoneDTO.setTotalPlaces(
                placeService.countAllPlacesByZoneID(updatedZoneDTO.getId()));
        updatedZoneDTO.setFreePlaces(
                placeService.countFreePlacesByZoneID(updatedZoneDTO.getId())
        );
        return updatedZoneDTO;
    }

    @PutMapping("places/{id}/status")
    @MutationMapping("changePlaceStatus")
    @Operation(summary = "Change place status")
    public PlaceDTO changePlaceStatus(
            @PathVariable("id")
            @Argument final Long placeId,
            @RequestParam
            @Argument final String status) {

        Place disabledPlace = placeService
                .changeStatus(placeId, Status.valueOf(status));

        return placeMapper.toDTO(disabledPlace);
    }

    @DeleteMapping("zones/{id}")
    @MutationMapping("deleteZoneAndAssociatedPlacesByZoneId")
    @Operation(summary = "Delete zone and associated places")
    public void deleteZoneAndAssociatedPlaces(
            @PathVariable("id")
            @Argument final Long zoneId) {
        zoneService.delete(zoneId);
    }

    @DeleteMapping("places/{id}")
    @MutationMapping("deletePlaceById")
    @Operation(summary = "Delete place by id")
    public void deletePlaceById(
            @PathVariable("id")
            @Argument final Long placeId) {
        placeService.delete(placeId);
    }

}

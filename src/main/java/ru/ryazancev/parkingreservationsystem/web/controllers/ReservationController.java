package ru.ryazancev.parkingreservationsystem.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.services.ReservationService;
import ru.ryazancev.parkingreservationsystem.web.dto.ReservationDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.validation.OnUpdate;
import ru.ryazancev.parkingreservationsystem.web.mappers.ReservationMapper;

import java.util.List;

@RestController
@RequestMapping("api/v1/reservations")
@RequiredArgsConstructor
@Validated
public class ReservationController {


    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;


    @GetMapping
    public List<ReservationDTO> getReservations() {
        List<Reservation> reservations = reservationService.getAll();
        return reservationMapper.toDTO(reservations);
    }

    @GetMapping("/{id}")
    public ReservationDTO getById(@PathVariable("id") Long id) {
        Reservation reservation = reservationService.getById(id);

        return reservationMapper.toDTO(reservation);
    }
    @PutMapping
    public ReservationDTO extendReservationByUserId(@Validated(OnUpdate.class) @RequestBody ReservationDTO reservationDTO) {
        Reservation reservation = reservationMapper.toEntity(reservationDTO);
        Reservation updatedReservation = reservationService.extend(reservation);

        return reservationMapper.toDTO(updatedReservation);
    }
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        reservationService.delete(id);
    }

}

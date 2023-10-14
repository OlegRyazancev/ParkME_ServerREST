package ru.ryazancev.parkingreservationsystem.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.services.ReservationService;
import ru.ryazancev.parkingreservationsystem.util.mappers.reservation.ReservationMapper;
import ru.ryazancev.parkingreservationsystem.util.validation.OnUpdate;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationDTO;

@RestController
@RequestMapping("api/v1/reservations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Reservation Controller", description = "Reservation API")
public class ReservationController {


    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    @PutMapping
    @Operation(summary = "Change reservation's time to")
    @PreAuthorize("@customSecurityExpression.canAccessReservation(#reservationDTO.id)")
    public ReservationDTO changeTimeTo(@Validated(OnUpdate.class) @RequestBody ReservationDTO reservationDTO) {
        Reservation reservation = reservationMapper.toEntity(reservationDTO);
        Reservation updatedReservation = reservationService.changeTimeTo(reservation);

        return reservationMapper.toDTO(updatedReservation);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reservation by id")
    @PreAuthorize("@customSecurityExpression.canAccessReservation(#id)")
    public void deleteById(@PathVariable("id") Long id) {
        reservationService.delete(id);
    }
}

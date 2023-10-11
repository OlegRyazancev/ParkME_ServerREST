package ru.ryazancev.parkingreservationsystem.util.mappers.reservation;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    ReservationDTO toDTO(Reservation reservation);

    List<ReservationDTO> toDTO(List<Reservation> reservations);

    Reservation toEntity(ReservationDTO reservationDTO);

}

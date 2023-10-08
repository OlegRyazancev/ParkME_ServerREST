package ru.ryazancev.parkingreservationsystem.web.mappers;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.web.dto.ReservationDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    ReservationDTO toDTO(Reservation reservation);

    List<ReservationDTO> toDTO(List<Reservation> reservations);

    Reservation toEntity(ReservationDTO reservationDTO);

}

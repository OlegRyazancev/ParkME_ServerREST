package ru.ryazancev.parkingreservationsystem.util.mappers;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationDTO;

@Mapper(componentModel = "spring")
public interface ReservationMapper
        extends Mappable<Reservation, ReservationDTO> {

}

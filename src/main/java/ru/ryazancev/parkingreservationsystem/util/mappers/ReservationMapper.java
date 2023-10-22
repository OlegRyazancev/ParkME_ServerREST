package ru.ryazancev.parkingreservationsystem.util.mappers;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.util.mappers.Mappable;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationDTO;

import java.util.List;

@Mapper(componentModel = "spring")
@SuppressWarnings("unmappedTargetProperties")
public interface ReservationMapper extends Mappable<Reservation, ReservationDTO> {

}

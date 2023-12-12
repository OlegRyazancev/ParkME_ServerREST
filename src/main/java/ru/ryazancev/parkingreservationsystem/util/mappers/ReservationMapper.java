package ru.ryazancev.parkingreservationsystem.util.mappers;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationDTO;

@Mapper(config = BaseMapperConfig.class)
public interface ReservationMapper
        extends Mappable<Reservation, ReservationDTO> {

}

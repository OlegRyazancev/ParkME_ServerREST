package ru.ryazancev.parkingreservationsystem.web.mappers.reservation;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationInfoDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationInfoMapper {

    @Mapping(target = "zone", source = "zone")
    @Mapping(target = "place", source = "place")
    @Mapping(target = "car", source = "car")
    ReservationInfoDTO toDTO(Reservation reservation);

    @Mapping(target = "zone", source = "zone")
    @Mapping(target = "place", source = "place")
    @Mapping(target = "car", source = "car")
    List<ReservationInfoDTO> toDTO(List<Reservation> reservations);

    @IterableMapping(qualifiedByName = "toDTO")
    Reservation toInfoEntity(ReservationInfoDTO reservationInfoDTO);
}

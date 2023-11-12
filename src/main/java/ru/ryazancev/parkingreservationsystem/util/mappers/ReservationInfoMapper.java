package ru.ryazancev.parkingreservationsystem.util.mappers;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationInfoDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationInfoMapper
        extends Mappable<Reservation, ReservationInfoDTO> {


    @Mapping(target = "zone", source = "zone")
    @Mapping(target = "place", source = "place")
    @Mapping(target = "car", source = "car")
    @Mapping(target = "user", source = "user")
    ReservationInfoDTO toDTO(Reservation reservation);

    @Mapping(target = "zone", source = "zone")
    @Mapping(target = "place", source = "place")
    @Mapping(target = "car", source = "car")
    @Mapping(target = "user", source = "user")
    List<ReservationInfoDTO> toDTO(List<Reservation> reservations);


    @IterableMapping(qualifiedByName = "toDTO")
    Reservation toEntity(ReservationInfoDTO reservationInfoDTO);
}

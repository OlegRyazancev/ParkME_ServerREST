package ru.ryazancev.parkingreservationsystem.util.mappers;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.web.dto.zone.ZoneDTO;

@Mapper(componentModel = "spring")
public interface ZoneMapper extends Mappable<Zone, ZoneDTO> {

}

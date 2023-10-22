package ru.ryazancev.parkingreservationsystem.util.mappers;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.util.mappers.Mappable;
import ru.ryazancev.parkingreservationsystem.web.dto.zone.ZoneDTO;

import java.util.List;

@Mapper(componentModel = "spring")
@SuppressWarnings("unmappedTargetProperties")
public interface ZoneMapper extends Mappable<Zone, ZoneDTO> {

}

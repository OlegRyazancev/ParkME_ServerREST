package ru.ryazancev.parkingreservationsystem.web.mappers.zone;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.web.dto.zone.ZoneDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ZoneMapper {

    ZoneDTO toDTO(Zone zone);

    List<ZoneDTO> toDTO(List<Zone> zones);

    Zone toEntity(ZoneDTO zoneDTO);

}

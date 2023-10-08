package ru.ryazancev.parkingreservationsystem.web.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.web.dto.ZoneDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ZoneMapper {

    ZoneDTO toDTO(Zone zone);

    List<ZoneDTO> toDTO(List<Zone> zones);

    Zone toEntity(ZoneDTO zoneDTO);

}

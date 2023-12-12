package ru.ryazancev.parkingreservationsystem.util.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.web.dto.zone.ZoneInfoDTO;

import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public interface ZoneInfoMapper extends Mappable<Zone, ZoneInfoDTO> {

    @Mapping(target = "places", source = "places")
    ZoneInfoDTO toDTO(Zone zone);

    @Mapping(target = "places", source = "places")
    List<ZoneInfoDTO> toDTO(List<Zone> zones);
}

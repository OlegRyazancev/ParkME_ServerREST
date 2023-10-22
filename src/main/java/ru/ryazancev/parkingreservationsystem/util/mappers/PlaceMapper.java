package ru.ryazancev.parkingreservationsystem.util.mappers;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.util.mappers.Mappable;
import ru.ryazancev.parkingreservationsystem.web.dto.place.PlaceDTO;

import java.util.List;

@Mapper(componentModel = "spring")
@SuppressWarnings("unmappedTargetProperties")
public interface PlaceMapper extends Mappable<Place, PlaceDTO> {

}

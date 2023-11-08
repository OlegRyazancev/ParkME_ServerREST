package ru.ryazancev.parkingreservationsystem.util.mappers;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.web.dto.place.PlaceDTO;

@Mapper(componentModel = "spring")
public interface PlaceMapper extends Mappable<Place, PlaceDTO> {

}

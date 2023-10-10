package ru.ryazancev.parkingreservationsystem.util.mappers.place;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.web.dto.place.PlaceInfoDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlaceInfoMapper {

    PlaceInfoDTO toDTO(Place place);

    List<PlaceInfoDTO> toDTO(List<Place> places);

    Place toEntity(PlaceInfoDTO placeInfoDTO);
}

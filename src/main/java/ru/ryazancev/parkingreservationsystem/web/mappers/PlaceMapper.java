package ru.ryazancev.parkingreservationsystem.web.mappers;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.web.dto.PlaceDTO;

import java.util.List;

@Mapper
public interface PlaceMapper {

    PlaceDTO toDTO(Place place);

    List<PlaceDTO> toDTO(List<Place> places);

    Place toEntity(PlaceDTO placeDTO);
}

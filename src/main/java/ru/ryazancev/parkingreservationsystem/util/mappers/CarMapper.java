package ru.ryazancev.parkingreservationsystem.util.mappers;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.util.mappers.Mappable;
import ru.ryazancev.parkingreservationsystem.web.dto.car.CarDTO;

import java.util.List;

@Mapper(componentModel = "spring")
@SuppressWarnings("unmappedTargetProperties")
public interface CarMapper extends Mappable<Car, CarDTO> {

}

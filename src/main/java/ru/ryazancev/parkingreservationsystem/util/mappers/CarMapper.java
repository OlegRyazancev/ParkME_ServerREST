package ru.ryazancev.parkingreservationsystem.util.mappers;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.web.dto.car.CarDTO;

@Mapper(componentModel = "spring")
public interface CarMapper extends Mappable<Car, CarDTO> {

}

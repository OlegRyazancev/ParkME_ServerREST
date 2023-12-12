package ru.ryazancev.parkingreservationsystem.util.mappers;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.web.dto.car.CarDTO;

@Mapper(config = BaseMapperConfig.class)
public interface CarMapper extends Mappable<Car, CarDTO> {

}

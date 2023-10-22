package ru.ryazancev.parkingreservationsystem.util.mappers;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.util.mappers.Mappable;
import ru.ryazancev.parkingreservationsystem.web.dto.user.UserDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, UserDTO> {


}

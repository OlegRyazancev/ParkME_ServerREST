package ru.ryazancev.parkingreservationsystem.util.mappers;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.web.dto.user.UserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, UserDTO> {


}

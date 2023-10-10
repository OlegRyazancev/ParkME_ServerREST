package ru.ryazancev.parkingreservationsystem.web.mappers.user;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.web.dto.user.UserDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    List<UserDTO> toDTO(List<User> users);

    User toEntity(UserDTO userDTO);
}

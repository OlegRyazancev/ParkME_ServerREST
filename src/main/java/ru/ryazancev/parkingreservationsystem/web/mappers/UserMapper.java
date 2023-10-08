package ru.ryazancev.parkingreservationsystem.web.mappers;

import org.mapstruct.Mapper;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.web.dto.UserDTO;

import java.util.List;

@Mapper
public interface UserMapper {

    UserDTO toDTO(User user);

    List<UserDTO> toDTO(List<User> users);

    User toEntity(UserDTO userDTO);
}

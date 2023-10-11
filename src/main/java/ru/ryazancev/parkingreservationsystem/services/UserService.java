package ru.ryazancev.parkingreservationsystem.services;

import ru.ryazancev.parkingreservationsystem.models.user.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAll();

    User getByUsername(String username);

    User getById(Long userId);

    User update(User user);

    User create(User user);

    void delete(Long userId);
}

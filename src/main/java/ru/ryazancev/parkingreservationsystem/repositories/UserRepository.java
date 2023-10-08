package ru.ryazancev.parkingreservationsystem.repositories;

import ru.ryazancev.parkingreservationsystem.models.user.Role;
import ru.ryazancev.parkingreservationsystem.models.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByName(String name);

    void update(User user);

    void create(User user);

    void insertUserRole(Long userId, Role role);

    void delete(Long userId);

}

package ru.ryazancev.parkingreservationsystem.repositories;

import ru.ryazancev.parkingreservationsystem.models.user.Role;
import ru.ryazancev.parkingreservationsystem.models.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String name);

    Boolean isCarOwner(Long userId, Long carId);

    Boolean isReservationOwner(Long userId, Long reservationId);

    void insertUserRole(Long userId, Role role);

    void create(User user);

    void update(User user);

    void delete(Long userId);

}


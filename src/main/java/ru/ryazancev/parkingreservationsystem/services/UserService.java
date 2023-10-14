package ru.ryazancev.parkingreservationsystem.services;

import ru.ryazancev.parkingreservationsystem.models.user.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getByUsername(String username);

    User getById(Long userId);

    User update(User user);

    User create(User user);

    boolean isCarOwner(Long userId, Long carId);

    boolean isReservationOwner(Long userId, Long reservationId);

    void delete(Long userId);
}

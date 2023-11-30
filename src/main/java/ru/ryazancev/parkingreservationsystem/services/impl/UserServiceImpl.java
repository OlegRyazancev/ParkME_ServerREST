package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.user.Role;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.repositories.UserRepository;
import ru.ryazancev.parkingreservationsystem.services.ReservationService;
import ru.ryazancev.parkingreservationsystem.services.UserService;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CarRepository carRepository;

    private final ReservationService reservationService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    @Override
    public User getByUsername(final String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    @Transactional
    @Override
    public User create(final User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException(
                    "User with this email already exists");
        }

        if (!user.getPassword().equals(user.getPasswordConfirmation())) {
            throw new IllegalStateException(
                    "Password and password confirmation do not equals");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> roles = Set.of(Role.ROLE_USER);
        user.setRoles(roles);
        userRepository.save(user);

        return user;
    }

    @Override
    public boolean isCarOwner(final Long userId,
                              final Long carId) {
        return userRepository.isCarOwner(userId, carId);
    }

    @Override
    public boolean isReservationOwner(final Long userId,
                                      final Long reservationId) {
        return userRepository.isReservationOwner(userId, reservationId);
    }

    @Transactional
    @Override
    public User update(final User user) {
        User existingUser = getById(user.getId());
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(existingUser);

    }

    @Transactional
    @Override
    public void delete(final Long userId) {

        User userToDelete = getById(userId);
        Hibernate.initialize(userToDelete.getCars());
        Hibernate.initialize(userToDelete.getReservations());

        userToDelete.getReservations()
                .forEach(reservation ->
                        reservationService.delete(reservation.getId()));

        carRepository.deleteAll(userToDelete.getCars());
        userRepository.deleteById(userId);
    }
}

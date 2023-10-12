package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.user.Role;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;
import ru.ryazancev.parkingreservationsystem.repositories.UserRepository;
import ru.ryazancev.parkingreservationsystem.services.UserService;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final PlaceRepository placeRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    @Override
    public User create(User user) {
        if (userRepository.findByEmail(user.getName()).isPresent())
            throw new IllegalStateException("User already exists");

        if (!user.getPassword().equals(user.getPasswordConfirmation()))
            throw new IllegalStateException("Password and password confirmation do not equals");

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.create(user);
        Set<Role> roles = Set.of(Role.ROLE_USER);

        userRepository.insertUserRole(user.getId(), Role.ROLE_USER);
        user.setRoles(roles);

        return user;
    }

    @Transactional
    @Override
    public User update(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.update(user);

        return user;
    }

    @Transactional
    @Override
    public void delete(Long userId) {

        placeRepository.findAllOccupiedByUserId(userId)
                .forEach(place -> placeRepository.changeStatus(place, Status.FREE));
        reservationRepository.findAllByUserId(userId)
                .forEach(reservation -> reservationRepository.delete(reservation.getId()));
        carRepository.findAllByUserId(userId)
                .forEach(car -> carRepository.delete(car.getId()));
        userRepository.delete(userId);
    }
}

package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.user.Role;
import ru.ryazancev.parkingreservationsystem.models.user.User;
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
        return userRepository.findByName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    @Override
    public User update(User user) {
        userRepository.update(user);

        return user;
    }

    @Transactional
    @Override
    public User create(User user) {
        if (userRepository.findByName(user.getName()).isPresent())
            throw new IllegalStateException("User already exists");

        if (!user.getPassword().equals(user.getPasswordConfirmation()))
            throw new IllegalStateException("Password and password confirmation do not equals");

        userRepository.create(user);
        Set<Role> roles = Set.of(Role.ROLE_USER);
        userRepository.insertUserRole(user.getId(), Role.ROLE_USER);
        user.setRoles(roles);

        return user;
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }
}

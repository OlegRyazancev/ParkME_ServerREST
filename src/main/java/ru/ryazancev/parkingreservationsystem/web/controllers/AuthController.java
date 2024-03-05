package ru.ryazancev.parkingreservationsystem.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.services.AuthService;
import ru.ryazancev.parkingreservationsystem.services.UserService;
import ru.ryazancev.parkingreservationsystem.util.mappers.UserMapper;
import ru.ryazancev.parkingreservationsystem.util.validation.OnCreate;
import ru.ryazancev.parkingreservationsystem.web.dto.auth.JwtRequest;
import ru.ryazancev.parkingreservationsystem.web.dto.auth.JwtResponse;
import ru.ryazancev.parkingreservationsystem.web.dto.user.UserDTO;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Auth Controller", description = "Auth API")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;


    @PostMapping("/login")
    @Operation(summary = "Login")
    public JwtResponse login(
            @Validated
            @RequestBody final JwtRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    @Operation(summary = "Register")
    public UserDTO register(
            @Validated(OnCreate.class)
            @RequestBody final UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User createdUser = userService.create(user);

        return userMapper.toDTO(createdUser);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh")
    public JwtResponse refresh(
            @RequestBody final String refreshToken) {
        return authService.refresh(refreshToken);
    }

}

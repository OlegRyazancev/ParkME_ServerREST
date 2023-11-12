package ru.ryazancev.integration.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ryazancev.config.testutils.JsonUtils;
import ru.ryazancev.config.testutils.paths.APIPaths;
import ru.ryazancev.integration.BaseIT;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.repositories.UserRepository;
import ru.ryazancev.parkingreservationsystem.web.dto.auth.JwtRequest;
import ru.ryazancev.parkingreservationsystem.web.dto.user.UserDTO;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
public class AuthControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @DisplayName("Login")
    @Test
    public void testLogin_returnsJwtResponseJSON() throws Exception {
        //Arrange
        User userForLogin = findObjectForTests(userRepository, 1L);
        JwtRequest jwtRequest = JwtRequest.builder()
                .username(userForLogin.getEmail())
                .password("password")
                .build();

        String loginJson = JsonUtils.createJsonNodeForObject(jwtRequest, List.of("username", "password")).toString();

        //Act && Assert
        mockMvc.perform(post(APIPaths.LOGIN)
                        .content(loginJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userForLogin.getId()))
                .andExpect(jsonPath("$.username").value(userForLogin.getEmail()))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @DisplayName("Register")
    @Test
    public void testRegister_returnsNewUserJSON() throws Exception {
        //Arrange
        UserDTO userForRegister = UserDTO.builder()
                .name("register")
                .email("register@gmail.com")
                .password("password")
                .passwordConfirmation("password")
                .build();

        String userForRegisterJson = JsonUtils.createJsonNodeForObject(userForRegister, List.of("name", "email", "password", "passwordConfirmation")).toString();

        //Act && Assert
        mockMvc.perform(post(APIPaths.REGISTER)
                        .content(userForRegisterJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(userForRegister.getName()))
                .andExpect(jsonPath("$.email").value(userForRegister.getEmail()));

        //Assert
        Optional<User> registeredUser = userRepository.findByEmail(userForRegister.getEmail());
        assertTrue(registeredUser.isPresent());
    }

}

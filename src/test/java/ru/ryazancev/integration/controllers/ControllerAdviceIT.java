package ru.ryazancev.integration.controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ru.ryazancev.integration.BaseIT;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.web.dto.auth.JwtRequest;
import ru.ryazancev.parkingreservationsystem.web.dto.car.CarDTO;
import ru.ryazancev.testutils.paths.APIPaths;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.ryazancev.testutils.JsonUtils.createJsonNodeForObject;


@AutoConfigureMockMvc
public class ControllerAdviceIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;

    @DisplayName("Handle resource not found exception")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testHandleResourceNotFound() throws Exception {
        mockMvc.perform(get(APIPaths.USER_BY_ID, 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @DisplayName("Handle resource mapping exception")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testHandleResourceMappingException() throws Exception {
        //Arrange
        Car testCar = findObjectForTests(carRepository, 1L);
        CarDTO updatingCarDTO = CarDTO.builder()
                .id(testCar.getId())
                .number(testCar.getNumber())
                .build();
        String json = createJsonNodeForObject(updatingCarDTO, List.of("id", "number")).toString();

        //Act && Assert
        mockMvc.perform(put(APIPaths.CARS)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Car has the same number"));
    }

    @DisplayName("Handle access denied exception when the user try to get cars of another user")
    @Test
    @WithUserDetails("test2@gmail.com")
    public void testHandleAccessDeniedException() throws Exception {
        mockMvc.perform(get(APIPaths.USER_CARS, 1L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @DisplayName("Handle constraint violation exception")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testHandleConstraintViolationException() throws Exception {
        //Arrange
        Car testCar = findObjectForTests(carRepository, 1L);
        CarDTO updatingCarDTO = CarDTO.builder()
                .id(testCar.getId())
                .number("NOT CORRECT NUMBER")
                .build();

        String json = createJsonNodeForObject(updatingCarDTO, List.of("id", "number")).toString();

        //Act && Assert
        mockMvc.perform(put(APIPaths.CARS)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.number").value("Car number should be in this format: A000AA00"))
                .andExpect(jsonPath("$.errors.*", Matchers.hasSize(1)));
    }

    @DisplayName("Handle authentication exception in login with incorrect details")
    @Test
    public void testHandleAuthenticationException() throws Exception {
        //Arrange

        JwtRequest jwtRequest = JwtRequest.builder()
                .username("test1@gmail.com")
                .password("INCORRECT PASSWORD")
                .build();

        String json = createJsonNodeForObject(jwtRequest, List.of("username", "password")).toString();

        //Act && Assert
        mockMvc.perform(post(APIPaths.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication failed"));
    }
}

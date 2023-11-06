//TODO refactoring car controller
package ru.ryazancev.parkingreservationsystem.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.ryazancev.config.IntegrationTestBase;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.web.dto.car.CarDTO;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class CarControllerTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;

    private final String CAR_CONTROLLER_PATH = "/api/v1/cars";

    private final String CAR_BY_ID_PATH = CAR_CONTROLLER_PATH + "/{id}";

    private final Car CAR = Car.builder()
            .id(3L)
            .number("C000CC00")
            .build();


    @DisplayName("Get car by id with right user details")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetCarById_returnsStatusOkAndCarJSON() throws Exception {
        //Act && Assert
        mockMvc.perform(get(CAR_BY_ID_PATH, CAR.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CAR.getId()))
                .andExpect(jsonPath("$.number").value(CAR.getNumber()));
    }

    @DisplayName("Delete car by id when car has no reservations")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testDeleteCarById_returnsNothing() throws Exception {
        //Act && Assert
        mockMvc.perform(delete(CAR_BY_ID_PATH, CAR.getId()))
                .andExpect(status().isOk());
    }

    @DisplayName("Update car")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testUpdateCar_returnsStatusOkAndUpdatedCarJSON() throws Exception {
        //Arrange
        CarDTO updatingCarDTO = CarDTO.builder()
                .id(1L)
                .number("X000XX00")
                .build();
        String json = new ObjectMapper().writeValueAsString(updatingCarDTO);
        //Act
        mockMvc.perform(put(CAR_CONTROLLER_PATH)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatingCarDTO.getId()))
                .andExpect(jsonPath("$.number").value(updatingCarDTO.getNumber()));

        //Assert
        Optional<Car> updatedCar = carRepository.findById(updatingCarDTO.getId());
        assertTrue(updatedCar.isPresent());
        assertEquals(updatingCarDTO.getId(), updatedCar.get().getId());
        assertEquals(updatingCarDTO.getNumber(), updatedCar.get().getNumber());
    }

}
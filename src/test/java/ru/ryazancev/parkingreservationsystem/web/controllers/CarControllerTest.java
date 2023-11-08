package ru.ryazancev.parkingreservationsystem.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ru.ryazancev.config.IntegrationTestBase;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.web.dto.car.CarDTO;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.ryazancev.testutils.JsonUtils.*;

@AutoConfigureMockMvc
class CarControllerTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;

    private final String CAR_CONTROLLER_PATH = "/api/v1/cars";
    private final String CAR_BY_ID_PATH = CAR_CONTROLLER_PATH + "/{id}";
    private final Long CAR_FOR_TESTS_ID = 3L;

    private Car testCar;

    @BeforeEach
    public void setUp(){
        testCar = findObjectForTests(carRepository, CAR_FOR_TESTS_ID);
    }

    @DisplayName("Get car by id with right user details")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetCarById_returnsStatusOkAndCarJSON() throws Exception {
        //Act && Assert
        mockMvc.perform(get(CAR_BY_ID_PATH, testCar.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCar.getId()))
                .andExpect(jsonPath("$.number").value(testCar.getNumber()));
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
        String json = createJsonNodeForObject(updatingCarDTO, List.of("id", "number")).toString();

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

    @DisplayName("Delete car by id when car has no reservations")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testDeleteCarById_returnsNothing() throws Exception {
        //Act && Assert
        mockMvc.perform(delete(CAR_BY_ID_PATH, testCar.getId()))
                .andExpect(status().isOk());

        //Assert
        assertFalse(carRepository.existsById(testCar.getId()));
    }

}

package ru.ryazancev.parkingreservationsystem.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ru.ryazancev.config.IntegrationTestBase;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class ZoneControllerTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    private final String ZONES_CONTROLLER_PATH = "/api/v1/zones";
    private final String ZONE_BY_ID_PATH = ZONES_CONTROLLER_PATH + "/{id}";
    private final String ZONES_PLACES_PATH = ZONE_BY_ID_PATH + "/places";
    private final String ZONES_FREE_PLACES_PATH = ZONES_PLACES_PATH + "/free";

    private final Zone ZONE = Zone.builder()
            .id(1L)
            .number(1)
            .build();

    @DisplayName("Get zones")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetZones_returnsListOfAllZones() throws Exception {
        //Arrange
        List<Map<String, Object>> zones = List.of(
                Map.of("id", 1, "number", 1),
                Map.of("id", 2, "number", 2),
                Map.of("id", 3, "number", 3)
        );

        //Act && Assert
        mockMvc.perform(get(ZONES_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$", hasItems(zones.toArray())));
    }

    @DisplayName("Get zone by id with right user details")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetZoneById_returnsStatusIsOkAndZoneJSON() throws Exception {
        //Act && Assert
        mockMvc.perform(get(ZONE_BY_ID_PATH, ZONE.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ZONE.getId()))
                .andExpect(jsonPath("$.number").value(ZONE.getNumber()));
    }

    @DisplayName("Get places by zone id")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetPlacesByZoneId_returnsListOfPlaces() throws Exception {
        //Arrange
        List<Map<String, Object>> places = List.of(
                Map.of("id", 1, "number", 1, "status", "FREE"),
                Map.of("id", 2, "number", 2, "status", "FREE"),
                Map.of("id", 3, "number", 3, "status", "OCCUPIED")
        );

        //Act && Assert
        mockMvc.perform(get(ZONES_PLACES_PATH, ZONE.getId()))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$", hasItems(places.toArray())));
    }

    @DisplayName("Get free places by zone id")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetFreePlacesByZoneId_returnsListOfFreePlaces() throws Exception {
        //Arrange
        List<Map<String, Object>> freePlaces = List.of(
                Map.of("id", 1, "number", 1, "status", "FREE"),
                Map.of("id", 2, "number", 2, "status", "FREE")
        );

        //Act && Assert
        mockMvc.perform(get(ZONES_FREE_PLACES_PATH, ZONE.getId()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$", hasItems(freePlaces.toArray())));
    }
}
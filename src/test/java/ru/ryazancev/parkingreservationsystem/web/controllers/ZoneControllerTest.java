package ru.ryazancev.parkingreservationsystem.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ru.ryazancev.config.IntegrationTestBase;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.repositories.ZoneRepository;
import ru.ryazancev.testutils.JsonUtils;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class ZoneControllerTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ZoneRepository zoneRepository;

    private final String ZONES_CONTROLLER_PATH = "/api/v1/zones";
    private final String ZONE_BY_ID_PATH = ZONES_CONTROLLER_PATH + "/{id}";
    private final String ZONES_PLACES_PATH = ZONE_BY_ID_PATH + "/places";
    private final String ZONES_FREE_PLACES_PATH = ZONES_PLACES_PATH + "/free";

    private final Long ZONE_FOR_TESTS_ID = 1L;
    private Zone testZone;

    @BeforeEach
    public void setUp() {
        testZone = findObjectForTests(zoneRepository, ZONE_FOR_TESTS_ID);
    }

    @DisplayName("Get zones")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetZones_returnsListOfAllZones() throws Exception {
        //Arrange
        List<Zone> zones = zoneRepository.findAll();
        String zonesJson = JsonUtils.createJsonNodeForObjects(zones, List.of("id", "number")).toString();

        //Act && Assert
        mockMvc.perform(get(ZONES_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(content().json(zonesJson));
    }

    @DisplayName("Get zone by id with right user details")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetZoneById_returnsStatusIsOkAndZoneJSON() throws Exception {
        //Act && Assert
        mockMvc.perform(get(ZONE_BY_ID_PATH, testZone.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testZone.getId()))
                .andExpect(jsonPath("$.number").value(testZone.getNumber()));
    }

    @DisplayName("Get places by zone id")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetPlacesByZoneId_returnsListOfPlaces() throws Exception {
        //Arrange
        List<Place> zonesPlaces = testZone.getPlaces();
        String placesJson = JsonUtils.createJsonNodeForObjects(zonesPlaces, List.of("id", "number", "status")).toString();

        //Act && Assert
        mockMvc.perform(get(ZONES_PLACES_PATH, testZone.getId()))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(content().json(placesJson));
    }

    @DisplayName("Get free places by zone id")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetFreePlacesByZoneId_returnsListOfFreePlaces() throws Exception {
        //Arrange
        List<Place> freePlaces = testZone.getPlaces()
                .stream()
                .filter(place -> place.getStatus().equals(Status.FREE))
                .toList();
        String freePlacesJson = JsonUtils.createJsonNodeForObjects(freePlaces, List.of("id", "number", "status")).toString();

        //Act && Assert
        mockMvc.perform(get(ZONES_FREE_PLACES_PATH, testZone.getId()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(freePlacesJson));
    }

}
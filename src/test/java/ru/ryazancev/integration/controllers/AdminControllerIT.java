package ru.ryazancev.integration.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ru.ryazancev.config.testutils.JsonUtils;
import ru.ryazancev.config.testutils.paths.APIPaths;
import ru.ryazancev.integration.BaseIT;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.repositories.*;
import ru.ryazancev.parkingreservationsystem.web.dto.place.PlaceDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.zone.ZoneDTO;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
public class AdminControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private ZoneRepository zoneRepository;


    @DisplayName("Get cars by admin")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetCars_returnsListOfCars() throws Exception {
        //Arrange
        List<Car> cars = carRepository.findAll();
        String carsJson = JsonUtils.createJsonNodeForObjects(cars, List.of("id", "number")).toString();

        //Act && Assert
        mockMvc.perform(get(APIPaths.ADMIN_CARS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(content().json(carsJson));
    }

    @DisplayName("Get users by admin")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetUsers_returnsListOfUsers() throws Exception {
        //Arrange
        List<User> users = userRepository.findAll();
        String usersJson = JsonUtils.createJsonNodeForObjects(users, List.of("id", "name", "email")).toString();

        //Act && Assert
        mockMvc.perform(get(APIPaths.ADMIN_USERS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(usersJson));
    }

    @DisplayName("Get reservations by admin")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetReservations_returnsListOfReservations() throws Exception {
        //Arrange
        List<Reservation> reservations = reservationRepository.findAll();
        String reservationsJson = JsonUtils.createJsonNodeForObjects(reservations, List.of("id", "timeFrom", "timeTo")).toString();

        //Act && Assert
        mockMvc.perform(get(APIPaths.ADMIN_RESERVATIONS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(reservationsJson));
    }

    @DisplayName("Get reservation info by id by admin")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetReservationInfoById_returnsReservationInfoJson() throws Exception {
        //Arrange
        Reservation reservation = findObjectForTests(reservationRepository, 1L);
        String fullReservationInfoJson = JsonUtils.createJsonNodeForObject(reservation, List.of("id", "timeFrom", "timeTo", "zone", "place", "car", "user")).toString();
        String extractedReservationInfoJson = JsonUtils.extractJson(fullReservationInfoJson);

        //Act && Assert
        mockMvc.perform(get(APIPaths.ADMIN_RESERVATION_BY_ID, reservation.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(extractedReservationInfoJson));
    }

    @DisplayName("Get place by id")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testGetPlaceById_returnsPlaceJson() throws Exception {
        //Arrange
        Place place = findObjectForTests(placeRepository, 1L);
        String placeJson = JsonUtils.createJsonNodeForObject(place, List.of("id", "number", "status")).toString();

        //Act && Assert
        mockMvc.perform(get(APIPaths.ADMIN_PLACE_BY_ID, place.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(placeJson));
    }

    @DisplayName("Create zone")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testCreateZone_returnsNewZoneJSONWithId() throws Exception {
        //Arrange
        ZoneDTO creatingZone = ZoneDTO.builder()
                .number(4)
                .build();
        String zoneJson = JsonUtils.createJsonNodeForObject(creatingZone, List.of("number")).toString();

        //Act && Assert
        mockMvc.perform(post(APIPaths.ADMIN_ZONES)
                        .content(zoneJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.number").value(creatingZone.getNumber()));

        //Assert
        Optional<Zone> createdZone = zoneRepository.findByNumber(creatingZone.getNumber());
        assertTrue(createdZone.isPresent());
        assertEquals(creatingZone.getNumber(), createdZone.get().getNumber());
    }

    @DisplayName("Create place in zone")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testCreatePlaceInZone_returnsCreatedPlaceJSONWithIdAndStatusFree() throws Exception {
        //Arrange
        Zone zone = findObjectForTests(zoneRepository, 1L);
        int countZonesPlaces = zone.getPlaces().size();

        PlaceDTO creatingPlace = PlaceDTO.builder()
                .number(4)
                .build();
        String placeJson = JsonUtils.createJsonNodeForObject(creatingPlace, List.of("number")).toString();

        //Act && Assert
        mockMvc.perform(post(APIPaths.ADMIN_ZONE_PLACES, zone.getId())
                        .content(placeJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.number").value(creatingPlace.getNumber()))
                .andExpect(jsonPath("$.status").value(Status.FREE.toString()));

        //Assert
        List<Place> zonePlaces = placeRepository.findAllByZoneId(zone.getId());
        assertEquals(++countZonesPlaces, zonePlaces.size());

        Optional<Place> createdPlace = zonePlaces
                .stream()
                .filter(place ->
                        place.getNumber().equals(creatingPlace.getNumber()))
                .findFirst();
        assertTrue(createdPlace.isPresent());
        assertEquals(creatingPlace.getNumber(), createdPlace.get().getNumber());
        assertEquals(Status.FREE, createdPlace.get().getStatus());
    }

    @DisplayName("Update zone")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testUpdateZone_returnsUpdatedZoneJSON() throws Exception {
        //Arrange
        Zone zoneToUpdate = findObjectForTests(zoneRepository, 1L);
        ZoneDTO updatingZone = ZoneDTO.builder()
                .id(zoneToUpdate.getId())
                .number(999)
                .build();
        String zoneJson = JsonUtils.createJsonNodeForObject(updatingZone, List.of("id", "number")).toString();

        //Act && Assert
        mockMvc.perform(put(APIPaths.ADMIN_ZONES, zoneToUpdate.getId())
                        .content(zoneJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatingZone.getId()))
                .andExpect(jsonPath("$.number").value(updatingZone.getNumber()));

        //Assert
        Optional<Zone> updatedZone = zoneRepository.findById(updatingZone.getId());
        assertTrue(updatedZone.isPresent());
        assertEquals(updatingZone.getId(), updatedZone.get().getId());
        assertEquals(updatingZone.getNumber(), updatedZone.get().getNumber());
    }

    @DisplayName("Change place status")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testChangePlaceStatus_returnsPlaceJSONWithChangedStatus() throws Exception {
        //Arrange
        Place placeToUpdate = findObjectForTests(placeRepository, 1L);
        String status = Status.DISABLE.name();

        //Act && Assert
        mockMvc.perform(put(APIPaths.ADMIN_PLACE_STATUS, placeToUpdate.getId())
                        .param("status", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(placeToUpdate.getId()))
                .andExpect(jsonPath("$.number").value(placeToUpdate.getNumber()))
                .andExpect(jsonPath("$.status").value(status));

        //Assert
        Optional<Place> updatedPlace = placeRepository.findById(placeToUpdate.getId());
        assertTrue(updatedPlace.isPresent());
        assertEquals(placeToUpdate.getNumber(), updatedPlace.get().getNumber());
        assertEquals(Status.DISABLE, updatedPlace.get().getStatus());
    }

    @DisplayName("Delete zone and associated places")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testDeleteZoneWithPlaces_returnsNothing() throws Exception {
        //Arrange
        Zone zoneToDelete = findObjectForTests(zoneRepository, 2L);

        //Act && Assert
        mockMvc.perform(delete(APIPaths.ADMIN_ZONE_BY_ID, zoneToDelete.getId()))
                .andExpect(status().isOk());

        //Assert
        assertFalse(zoneRepository.existsById(zoneToDelete.getId()));
        assertTrue(placeRepository.findAllByZoneId(zoneToDelete.getId()).isEmpty());

    }

    @DisplayName("Delete place by id")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testDeletePlaceById_returnsNothing() throws Exception {
        //Arrange
        Long placeToDeleteId = 1L;

        //Act && Assert
        mockMvc.perform(delete(APIPaths.ADMIN_PLACE_BY_ID, placeToDeleteId))
                .andExpect(status().isOk());

        //Assert
        assertFalse(placeRepository.existsById(placeToDeleteId));
    }
}

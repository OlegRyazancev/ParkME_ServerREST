package ru.ryazancev.parkingreservationsystem.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.node.ObjectNode;
import ru.ryazancev.config.IntegrationTestBase;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;
import ru.ryazancev.parkingreservationsystem.repositories.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
class UserControllerTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private ReservationRepository reservationRepository;


    private final ObjectMapper OBJECTMAPPER = new ObjectMapper();
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final String USER_CONTROLLER_PATH = "/api/v1/users";
    private final String USER_BY_ID_PATH = USER_CONTROLLER_PATH + "/{id}";
    private final String USER_CARS_PATH = USER_BY_ID_PATH + "/cars";
    private final String USER_RESERVATIONS_PATH = USER_BY_ID_PATH + "/reservations";

    private final User USER = User.builder()
            .id(1L)
            .name("Test1")
            .email("test1@gmail.com")
            .build();


    @DisplayName("Get user by id")
    @Test
    @WithUserDetails("test1@gmail.com")
    void testGetUserById_returnsUserJSON() throws Exception {
        //Act && Assert
        mockMvc.perform(get(USER_BY_ID_PATH, USER.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER.getId()))
                .andExpect(jsonPath("$.name").value(USER.getName()))
                .andExpect(jsonPath("$.email").value(USER.getEmail()));
    }


    @DisplayName("Get cars by user id")
    @Test
    @WithUserDetails("test1@gmail.com")
    void testGetCarsByUserId_returnsListOfCars() throws Exception {
        //Arrange
        List<Map<String, Object>> userCars = List.of(
                Map.of("id", 1, "number", "A000AA00"),
                Map.of("id", 2, "number", "B000BB00"),
                Map.of("id", 3, "number", "C000CC00")
        );

        //Act && Assert
        mockMvc.perform(get(USER_CARS_PATH, USER.getId()))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$", hasItems(userCars.toArray())));
    }

    @DisplayName("Get reservations by user id")
    @Test
    @WithUserDetails("test1@gmail.com")
    void testGetReservationsByUserId_returnsListOfReservations() throws Exception {
        //Arrange
        List<Map<String, Object>> userReservations = List.of(
                Map.of("id", 1, "timeFrom", "2024-01-01 12:00", "timeTo", "2024-01-23 14:00"),
                Map.of("id", 2, "timeFrom", "2025-01-29 10:54", "timeTo", "2025-01-30 12:00")
        );

        //Act && Assert
        mockMvc.perform(get(USER_RESERVATIONS_PATH, USER.getId()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$", hasItems(userReservations.toArray())));
    }

    @DisplayName("Create car")
    @Test
    @WithUserDetails("test1@gmail.com")
    void testCreateCar_returnsNewCarJSONWithId() throws Exception {
        //Arrange
        Car car = Car.builder().number("X000XX00").build();

        String json = OBJECTMAPPER.writeValueAsString(car);

        //Act && Assert
        mockMvc.perform(post(USER_CARS_PATH, USER.getId())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.number").value(car.getNumber()));

        //Assert
        Optional<Car> createdCar = getCreatedCar(USER.getId(), car.getNumber());
        assertTrue(createdCar.isPresent());
        assertEquals(car.getNumber(), createdCar.get().getNumber());


    }

    @DisplayName("Make reservation")
    @Test
    @WithUserDetails("test1@gmail.com")
    void testMakeReservation_returnsNewReservationJSONWithId() throws Exception {
        //Arrange
        LocalDateTime timeFrom = LocalDateTime.of(2030, 1, 29, 10, 54);
        LocalDateTime timeTo = LocalDateTime.of(2030, 8, 10, 10, 0);
        Zone zone = Zone.builder().id(1L).number(1).build();
        Place place = Place.builder().number(2).build();
        Car car = Car.builder().number("C000CC00").build();

        String json = createJsonNodeForReservation(timeFrom, timeTo, zone, place, car).toString();

        //Act && Assert
        mockMvc.perform(post(USER_RESERVATIONS_PATH, USER.getId())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.timeFrom").value(timeFrom.format(FORMATTER)))
                .andExpect(jsonPath("$.timeTo").value(timeTo.format(FORMATTER)));

        //Assert
        Optional<Place> occupiedPlace = getOccupiedPlace(zone.getId(), place.getNumber());
        assertTrue(occupiedPlace.isPresent());
        assertEquals(Status.OCCUPIED, occupiedPlace.get().getStatus());

        Optional<Reservation> createdReservation = getCreatedReservation(USER.getId(), occupiedPlace.get().getId());
        assertTrue(createdReservation.isPresent());
        assertEquals(timeFrom, createdReservation.get().getTimeFrom());
        assertEquals(timeTo, createdReservation.get().getTimeTo());
    }

    @DisplayName("Update user")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testUpdateUser_returnsStatusOkAndUpdatedUserJSON() throws Exception {
        //Arrange
        User updatingUser = User.builder()
                .id(1L)
                .name("New name")
                .email("test1@gmail.com")
                .password("password")
                .build();
        String json = new ObjectMapper().writeValueAsString(updatingUser);
        //Act
        mockMvc.perform(put(USER_CONTROLLER_PATH)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatingUser.getId()))
                .andExpect(jsonPath("$.name").value(updatingUser.getName()))
                .andExpect(jsonPath("$.email").value(updatingUser.getEmail()));

        //Assert
        Optional<User> updatedUser = userRepository.findById(updatingUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(updatingUser.getId(), updatedUser.get().getId());
        assertEquals(updatingUser.getName(), updatedUser.get().getName());
    }

    @DisplayName("Delete user")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testDeleteUser_shouldDeleteAllUsersDependencies_returnsNothing() throws Exception {
        //Act && Assert
        mockMvc.perform(delete(USER_BY_ID_PATH, USER.getId()))
                .andExpect(status().isOk());

        //Assert
        List<Car> cars = carRepository.findAllByUserId(USER.getId());
        assertTrue(cars.isEmpty());

        List<Reservation> reservations = reservationRepository.findAllByUserId(USER.getId());
        assertTrue(reservations.isEmpty());

        List<Place> places = placeRepository.findAllOccupiedByUserId(USER.getId());
        assertTrue(places.isEmpty());

        Optional<User> user = userRepository.findById(USER.getId());
        assertTrue(user.isEmpty());
    }


    private ObjectNode createJsonNodeForReservation(
            LocalDateTime timeFrom,
            LocalDateTime timeTo,
            Zone zone,
            Place place,
            Car car
    ) {
        ObjectNode json = OBJECTMAPPER.createObjectNode();

        json.put("timeFrom", timeFrom.format(FORMATTER));
        json.put("timeTo", timeTo.format(FORMATTER));

        ObjectNode zoneNode = OBJECTMAPPER.createObjectNode();
        zoneNode.put("number", zone.getNumber());
        json.set("zone", zoneNode);

        ObjectNode placeNode = OBJECTMAPPER.createObjectNode();
        placeNode.put("number", place.getNumber());
        json.set("place", placeNode);

        ObjectNode carNode = OBJECTMAPPER.createObjectNode();
        carNode.put("number", car.getNumber());
        json.set("car", carNode);

        return json;
    }

    private Optional<Place> getOccupiedPlace(Long zoneId, Integer placeNumber) {
        return placeRepository.findAllByZoneId(zoneId)
                .stream()
                .filter(p -> p.getNumber().equals(placeNumber))
                .findFirst();
    }

    private Optional<Reservation> getCreatedReservation(Long userId, Long placeId) {
        return reservationRepository.findAllByUserId(userId)
                .stream()
                .filter(reservation -> reservation
                        .getPlace()
                        .getId()
                        .equals(placeId))
                .findFirst();
    }

    private Optional<Car> getCreatedCar(Long userId, String carNumber) {
        return carRepository.findAllByUserId(userId)
                .stream()
                .filter(c -> c.getNumber().equals(carNumber))
                .findFirst();
    }
}
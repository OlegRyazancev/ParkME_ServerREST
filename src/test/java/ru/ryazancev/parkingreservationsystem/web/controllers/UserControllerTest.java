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
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;
import ru.ryazancev.parkingreservationsystem.repositories.UserRepository;
import ru.ryazancev.parkingreservationsystem.web.dto.car.CarDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.place.PlaceInfoDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationInfoDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.zone.ZoneDTO;
import ru.ryazancev.testutils.DateUtils;
import ru.ryazancev.testutils.JsonUtils;

import java.time.LocalDateTime;
import java.util.List;
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

    private final Long USER_FOR_TESTS_ID = 1L;
    private final String USER_CONTROLLER_PATH = "/api/v1/users";
    private final String USER_BY_ID_PATH = USER_CONTROLLER_PATH + "/{id}";
    private final String USER_CARS_PATH = USER_BY_ID_PATH + "/cars";
    private final String USER_RESERVATIONS_PATH = USER_BY_ID_PATH + "/reservations";

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = findObjectForTests(userRepository, USER_FOR_TESTS_ID);
    }

    @DisplayName("Get user by id")
    @Test
    @WithUserDetails("test1@gmail.com")
    void testGetUserById_returnsUserJSON() throws Exception {
        //Act && Assert
        mockMvc.perform(get(USER_BY_ID_PATH, testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.name").value(testUser.getName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }


    @DisplayName("Get cars by user id")
    @Test
    @WithUserDetails("test1@gmail.com")
    void testGetCarsByUserId_returnsListOfCars() throws Exception {
        //Arrange
        List<Car> userCars = testUser.getCars();
        String carsJson = JsonUtils.createJsonNodeForObjects(userCars, List.of("id", "number")).toString();

        //Act && Assert
        mockMvc.perform(get(USER_CARS_PATH, testUser.getId()))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(content().json(carsJson));
    }

    @DisplayName("Get reservations by user id")
    @Test
    @WithUserDetails("test1@gmail.com")
    void testGetReservationsByUserId_returnsListOfReservations() throws Exception {
        //Arrange
        List<Reservation> usersReservations = testUser.getReservations();
        String reservationsJson = JsonUtils.createJsonNodeForObjects(usersReservations, List.of("id", "timeFrom", "timeTo")).toString();

        //Act && Assert
        mockMvc.perform(get(USER_RESERVATIONS_PATH, testUser.getId()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(reservationsJson));
    }

    @DisplayName("Create car")
    @Test
    @WithUserDetails("test1@gmail.com")
    void testCreateCar_returnsNewCarJSONWithId() throws Exception {
        //Arrange
        CarDTO car = CarDTO.builder().number("X000XX00").build();
        String carJson = JsonUtils.createJsonNodeForObject(car, List.of("number")).toString();

        //Act && Assert
        mockMvc.perform(post(USER_CARS_PATH, testUser.getId())
                        .content(carJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.number").value(car.getNumber()));

        //Assert
        Optional<Car> createdCar = getCreatedCar(testUser.getId(), car.getNumber());
        assertTrue(createdCar.isPresent());
        assertEquals(car.getNumber(), createdCar.get().getNumber());
    }

    @DisplayName("Make reservation")
    @Test
    @WithUserDetails("test1@gmail.com")
    void testMakeReservation_returnsNewReservationJSONWithId() throws Exception {
        //Arrange
        ReservationInfoDTO reservationInfoDTO = ReservationInfoDTO.builder()
                .timeFrom(LocalDateTime.of(2030, 1, 29, 10, 54))
                .timeTo(LocalDateTime.of(2030, 8, 10, 10, 0))
                .zone(ZoneDTO.builder().id(1L).number(1).build())
                .place(PlaceInfoDTO.builder().number(2).build())
                .car(CarDTO.builder().number("C000CC00").build())
                .build();

        String json = JsonUtils.createJsonNodeForObject(reservationInfoDTO, List.of("timeFrom", "timeTo", "zone", "place", "car")).toString();

        //Act && Assert
        mockMvc.perform(post(USER_RESERVATIONS_PATH, testUser.getId())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.timeFrom").value(reservationInfoDTO.getTimeFrom().format(DateUtils.customFormatter)))
                .andExpect(jsonPath("$.timeTo").value(reservationInfoDTO.getTimeTo().format(DateUtils.customFormatter)));

        //Assert
        Optional<Place> occupiedPlace = getOccupiedPlace(reservationInfoDTO.getZone().getId(), reservationInfoDTO.getPlace().getNumber());
        assertTrue(occupiedPlace.isPresent());
        assertEquals(Status.OCCUPIED, occupiedPlace.get().getStatus());

        Optional<Reservation> createdReservation = getCreatedReservation(testUser.getId(), occupiedPlace.get().getId());
        assertTrue(createdReservation.isPresent());
        assertEquals(reservationInfoDTO.getTimeFrom(), createdReservation.get().getTimeFrom());
        assertEquals(reservationInfoDTO.getTimeTo(), createdReservation.get().getTimeTo());
    }

    @DisplayName("Update user")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testUpdateUser_returnsStatusOkAndUpdatedUserJSON() throws Exception {
        //Arrange
        User updatingUser = User.builder()
                .id(testUser.getId())
                .name("New name")
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .build();
        String json = JsonUtils.createJsonNodeForObject(updatingUser, List.of("id", "name", "email", "password")).toString();
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
        mockMvc.perform(delete(USER_BY_ID_PATH, testUser.getId()))
                .andExpect(status().isOk());

        //Assert
        List<Car> cars = carRepository.findAllByUserId(testUser.getId());
        assertTrue(cars.isEmpty());

        List<Reservation> reservations = reservationRepository.findAllByUserId(testUser.getId());
        assertTrue(reservations.isEmpty());

        List<Place> places = placeRepository.findAllOccupiedByUserId(testUser.getId());
        assertTrue(places.isEmpty());

        Optional<User> user = userRepository.findById(testUser.getId());
        assertTrue(user.isEmpty());
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
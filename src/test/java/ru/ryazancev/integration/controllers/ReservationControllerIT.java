package ru.ryazancev.integration.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ru.ryazancev.integration.BaseIT;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;
import ru.ryazancev.parkingreservationsystem.web.dto.reservation.ReservationDTO;
import ru.ryazancev.testutils.DateUtils;
import ru.ryazancev.testutils.JsonUtils;
import ru.ryazancev.testutils.paths.APIPaths;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ReservationControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PlaceRepository placeRepository;

    private final Long RESERVATION_ID_FOR_TESTS = 1L;

    private Reservation testReservation;

    @BeforeEach
    public void setUp() {
        testReservation = findObjectForTests(reservationRepository, RESERVATION_ID_FOR_TESTS);
    }

    @DisplayName("Change timeTo of reservation")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testChangeTimeTo_returnsStatusOkAndReservationJSON() throws Exception {
        //Arrange
        ReservationDTO updatingReservationDTO = ReservationDTO.builder()
                .id(testReservation.getId())
                .timeTo(LocalDateTime.of(2024, 2, 23, 14, 0, 0))
                .build();

        String reservationJson = JsonUtils.createJsonNodeForObject(
                        updatingReservationDTO,
                        List.of("id",
                                "timeTo"))
                .toString();

        //Act && Assert
        mockMvc.perform(put(APIPaths.RESERVATIONS)
                        .content(reservationJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(testReservation.getId()))
                .andExpect(jsonPath("$.timeFrom")
                        .value(testReservation.getTimeFrom()
                                .format(DateUtils.customFormatter)))
                .andExpect(jsonPath("$.timeTo")
                        .value(
                                updatingReservationDTO.getTimeTo()
                                        .format(DateUtils.customFormatter)));

        //Assert
        Optional<Reservation> updatedReservation =
                reservationRepository.findById(testReservation.getId());
        assertTrue(updatedReservation.isPresent());
        assertEquals(updatingReservationDTO.getId(), updatedReservation.get().getId());
        assertEquals(testReservation.getTimeFrom(), updatedReservation.get().getTimeFrom());
    }

    @DisplayName("Delete reservation")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testDeleteReservation_shouldDeleteAllDependencies_returnsNothing() throws Exception {
        //Act
        mockMvc.perform(delete(APIPaths.RESERVATION_BY_ID, testReservation.getId()))
                .andExpect(status().isOk());
        //Assert
        assertFalse(reservationRepository.existsById(testReservation.getId()));

        Optional<Place> placeAfterDeleteReservation =
                placeRepository.findById(testReservation.getPlace().getId());
        assertTrue(placeAfterDeleteReservation.isPresent());
        assertEquals(Status.FREE, placeAfterDeleteReservation.get().getStatus());

        List<Reservation> usersReservations =
                reservationRepository.findAllByUserId(testReservation.getUser().getId());
        assertTrue(usersReservations
                .stream()
                .noneMatch(reservation ->
                        reservation.getId().equals(testReservation.getId())));
    }
}

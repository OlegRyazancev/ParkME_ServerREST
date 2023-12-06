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
import ru.ryazancev.parkingreservationsystem.models.parking.PlaceStatus;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.reservation.ReservationStatus;
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

    private Reservation plannedReservation;

    @BeforeEach
    public void setUp() {
        plannedReservation = findObjectForTests(reservationRepository, RESERVATION_ID_FOR_TESTS);
    }

    @DisplayName("Change timeTo of reservation")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testChangeTimeTo_returnsStatusOkAndReservationJSON() throws Exception {
        //Arrange
        ReservationDTO updatingReservationDTO = ReservationDTO.builder()
                .id(plannedReservation.getId())
                .timeTo(LocalDateTime.of(2024, 11, 14, 14, 0, 0))
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
                        .value(plannedReservation.getId()))
                .andExpect(jsonPath("$.timeFrom")
                        .value(plannedReservation.getTimeFrom()
                                .format(DateUtils.customFormatter)))
                .andExpect(jsonPath("$.timeTo")
                        .value(
                                updatingReservationDTO.getTimeTo()
                                        .format(DateUtils.customFormatter)));

        //Assert
        Optional<Reservation> updatedReservation =
                reservationRepository.findById(plannedReservation.getId());
        assertTrue(updatedReservation.isPresent());
        assertEquals(updatingReservationDTO.getId(), updatedReservation.get().getId());
        assertEquals(plannedReservation.getTimeFrom(), updatedReservation.get().getTimeFrom());
    }

    @DisplayName("Cancel reservation")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testCancelReservation_ifReservationIsActive_shouldReturnCompletedReservation() throws Exception {
        //Act
        mockMvc.perform(put(APIPaths.RESERVATION_BY_ID, plannedReservation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(plannedReservation.getId()))
                .andExpect(jsonPath("$.timeFrom")
                        .value(plannedReservation.getTimeFrom()
                                .format(DateUtils.customFormatter)))
                .andExpect(jsonPath("$.timeTo")
                        .value(plannedReservation.getTimeTo()
                                .format(DateUtils.customFormatter)))
                .andExpect(jsonPath("$.status")
                        .value(ReservationStatus.COMPLETED.name()));

        //Assert
        Optional<Place> place = placeRepository
                .findById(plannedReservation.getPlace().getId());
        assertTrue(place.isPresent());
        assertEquals(PlaceStatus.FREE, place.get().getStatus());
    }

    @DisplayName("Cancel reservation")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testCancelReservation_ifReservationIsPlanned_shouldReturnCanceledReservation() throws Exception {
        //Arrange
        Reservation plannedReservation = findObjectForTests(reservationRepository, 11L);

        //Act
        mockMvc.perform(put(APIPaths.RESERVATION_BY_ID, plannedReservation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(plannedReservation.getId()))
                .andExpect(jsonPath("$.timeFrom")
                        .value(plannedReservation.getTimeFrom()
                                .format(DateUtils.customFormatter)))
                .andExpect(jsonPath("$.timeTo")
                        .value(plannedReservation.getTimeTo()
                                .format(DateUtils.customFormatter)))
                .andExpect(jsonPath("$.status")
                        .value(ReservationStatus.CANCELED.name()));
    }

    @DisplayName("Delete reservation")
    @Test
    @WithUserDetails("test1@gmail.com")
    public void testDeleteReservation_shouldDeleteAllDependencies_returnsNothing() throws Exception {
        //Act
        mockMvc.perform(delete(APIPaths.RESERVATION_BY_ID, plannedReservation.getId()))
                .andExpect(status().isOk());
        //Assert
        assertFalse(reservationRepository.existsById(plannedReservation.getId()));

        Optional<Place> placeAfterDeleteReservation =
                placeRepository.findById(plannedReservation.getPlace().getId());
        assertTrue(placeAfterDeleteReservation.isPresent());
        assertEquals(PlaceStatus.FREE, placeAfterDeleteReservation.get().getStatus());

        List<Reservation> usersReservations =
                reservationRepository.findAllByUserIdOrderByTimeFromDesc(
                        plannedReservation.getUser().getId());
        assertTrue(usersReservations
                .stream()
                .noneMatch(reservation ->
                        reservation.getId()
                                .equals(plannedReservation.getId())));
    }
}

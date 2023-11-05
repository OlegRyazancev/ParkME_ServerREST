package ru.ryazancev.parkingreservationsystem.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.repositories.*;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Reservation reservation;

    private Zone zone;
    private Place place;
    private Car car;
    private User user;

    @BeforeEach
    public void setup() {
        reservation = Reservation.builder().id(2L)
                .timeFrom(LocalDateTime.now())
                .timeTo(LocalDateTime.now().plusHours(7))
                .build();

        place = new Place(1L, 1, Status.FREE);
        zone = new Zone(1L, 1, List.of(place));
        car = new Car(1L, "XX000X00");
        user = User.builder().id(1L).build();

        reservation.setZone(zone);
        reservation.setUser(user);
        reservation.setPlace(place);
        reservation.setCar(car);
    }


    @DisplayName("Get all reservations")
    @Test
    public void testGetAllReservations_returnsListOfReservations() {
        //Arrange
        Reservation reservation2 = Reservation.builder()
                .id(2L)
                .timeFrom(LocalDateTime.now())
                .timeTo(LocalDateTime.now().plusHours(2))
                .build();
        List<Reservation> sampleReservations = List.of(reservation, reservation2);
        when(reservationRepository.findAll()).thenReturn(sampleReservations);

        //Act
        List<Reservation> reservations = reservationService.getAll();

        //Assert
        assertEquals(sampleReservations, reservations, "Returned list should be the same");
    }

    @DisplayName("Get reservation info by id")
    @Test
    public void testGetReservationInfoById_whenValidId_returnsReservationObject() {
        //Arrange
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        //Act
        Reservation foundReservation = reservationService.getInfo(reservation.getId());

        //Assert
        assertEquals(reservation, foundReservation, "Returned reservation should be the same");
    }

    @DisplayName("Get reservation info by not existing id")
    @Test
    public void testGetReservationInfo_whenNotValidId_throwsResourceNotFoundException() {
        //Arrange
        String expectedExceptionMessage = "Reservation not found";
        Long nonExistingId = 12L;
        when(reservationRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.getInfo(nonExistingId);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage());
    }

    @DisplayName("Get reservations by user id")
    @Test
    public void testGetReservationsByUserId_whenUserHasReservations_returnsReservations() {
        //Arrange
        Reservation reservation2 = Reservation.builder()
                .id(2L)
                .timeFrom(LocalDateTime.now())
                .timeTo(LocalDateTime.now().plusHours(2))
                .build();
        List<Reservation> sampleReservations = List.of(reservation, reservation2);
        when(reservationRepository.findAllByUserId(anyLong())).thenReturn(sampleReservations);

        //Act
        List<Reservation> reservations = reservationService.getReservationsByUserId(anyLong());

        //Assert
        assertEquals(sampleReservations, reservations, "Returned list should be the same");
    }

    @DisplayName("Get reservations by user id when user has no reservations")
    @Test
    public void testGetReservationsByUserId_whenUserHasNoReservations_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "User don't make any reservation";
        when(reservationRepository.findAllByUserId(anyLong())).thenReturn(Collections.emptyList());

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            reservationService.getReservationsByUserId(anyLong());
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Create reservation with valid details")
    @Test
    public void testCreateReservation_whenReservationDetailsAreValid_ReturnReservationObject() {
        //Arrange
        when(zoneRepository.findByNumber(any())).thenReturn(Optional.of(zone));
        when(placeRepository.save(any())).thenReturn(place);
        when(carRepository.findByNumber(any())).thenReturn(Optional.of(car));
        when(reservationRepository.findByCarId(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        //Act
        Reservation createdReservation = reservationService.create(reservation, user.getId());

        //Assert
        assertNotNull(createdReservation, "Created reservation should not be null");
        assertEquals(Status.OCCUPIED, createdReservation.getPlace().getStatus(), "Place status should be changed to OCCUPIED");

        verify(reservationRepository).save(reservation);
        verify(placeRepository).save(place);
    }

    @DisplayName("Create reservation if zone does not exists")
    @Test
    public void testCreateReservation_whenZoneDoesNotExist_throwsResourceNotFoundException() {
        //Arrange
        String expectedExceptionMessage = "No zone with the specified number";
        when(zoneRepository.findByNumber(any())).thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.create(reservation, anyLong());
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Create reservation if place does not exists")
    @Test
    public void testCreateReservation_whenZoneHasNoPlaceWithNumber_throwsResourceNotFoundException() {
        //Arrange
        String expectedExceptionMessage = "No place with the specified number in the selected zone";
        reservation.getPlace().setNumber(2);
        zone.setPlaces(List.of(new Place(1L, 1, Status.FREE)));

        when(zoneRepository.findByNumber(any())).thenReturn(Optional.of(zone));

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.create(reservation, anyLong());
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Create reservation on OCCUPIED place")
    @Test
    public void testCreateReservation_whenPlaceIsOccupied_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "Place is already occupied or disabled";
        zone.setPlaces(List.of(new Place(1L, 1, Status.OCCUPIED)));

        when(zoneRepository.findByNumber(any())).thenReturn(Optional.of(zone));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            reservationService.create(reservation, anyLong());
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Create reservation on DISABLED place")
    @Test
    public void testCreateReservation_whenPlaceIsDisabled_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "Place is already occupied or disabled";
        zone.setPlaces(List.of(new Place(1L, 1, Status.DISABLE)));

        when(zoneRepository.findByNumber(any())).thenReturn(Optional.of(zone));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            reservationService.create(reservation, anyLong());
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Create reservation if car does not exists")
    @Test
    public void testCreateReservation_whenCarNotExists_throwsResourceNotFoundException() {
        //Arrange
        String expectedExceptionMessage = "Car not found";
        zone.setPlaces(List.of(new Place(1L, 1, Status.FREE)));

        when(zoneRepository.findByNumber(any())).thenReturn(Optional.of(zone));
        when(carRepository.findByNumber(any())).thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.create(reservation, anyLong());
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }


    @DisplayName("Create reservation if car already has reservation")
    @Test
    public void testCreateReservation_whenCarHasReservation_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "Car already has a reservation";
        zone.setPlaces(List.of(new Place(1L, 1, Status.FREE)));

        when(zoneRepository.findByNumber(any())).thenReturn(Optional.of(zone));
        when(carRepository.findByNumber(any())).thenReturn(Optional.of(car));
        when(reservationRepository.findByCarId(anyLong())).thenReturn(Optional.of(new Reservation()));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            reservationService.create(reservation, anyLong());
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Change reservation's time to")
    @Test
    public void testChangeReservationTimeTo_whenReservationDetailsAreValid_returnsReservationObject() {
        //Arrange
        reservation.setTimeTo(LocalDateTime.now().plusHours(5));

        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        //Act
        Reservation result = reservationService.changeTimeTo(reservation);

        //Assert
        verify(reservationRepository).save(reservation);

        assertEquals(reservation, result, "Updated reservation should be equals to input reservation");
    }

    @DisplayName("Change  non-existing reservation's time to")
    @Test
    public void testChangeReservationTimeTo_whenReservationDoesNotExist_throwsResourceNotFoundException() {
        //Arrange
        String expectedExceptionMessage = "Reservation not found";
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.empty());

        //Act && Assert

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.changeTimeTo(reservation);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Change reservation's time to to before actual value")
    @Test
    public void testChangeReservationTimeTo_whenUpdatedTimeToIsBeforeActualValue_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "Can not extend reservation, because time from is before time to";
        reservation.setTimeTo(LocalDateTime.now().minusHours(2));
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            reservationService.changeTimeTo(reservation);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Delete reservation with valid details")
    @Test
    public void testDeleteReservation_whenReservationDetailsAreValid_returnsNothing() {
        //Arrange
        reservation.getPlace().setStatus(Status.DISABLE);
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        //Act
        reservationService.delete(reservation.getId());

        //Assert
        verify(placeRepository).save(reservation.getPlace());
        verify(reservationRepository).deleteById(reservation.getId());
        assertEquals(Status.FREE, reservation.getPlace().getStatus(), "Reservation's place status should be changed to FREE");
    }

    @DisplayName("Delete non-existing reservation")
    @Test
    public void testDeleteReservation_whenReservationDoesNotExist_throwsResourceNotFoundException() {
        //Arrange
        String expectedExceptionMessage = "Reservation not found";
        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.empty());

        //Act
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.delete(reservation.getId());
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }
}
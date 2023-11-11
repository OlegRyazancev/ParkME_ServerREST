package ru.ryazancev.unit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;
import ru.ryazancev.parkingreservationsystem.services.impl.CarServiceImpl;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
public class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private CarServiceImpl carService;

    private Car car;

    @BeforeEach
    public void setup() {
        car = Car.builder()
                .id(1L)
                .number("XX000X00")
                .build();
    }

    @DisplayName("Get all cars")
    @Test
    public void testGetAllCars_returnsListOfCars() {
        //Arrange
        Car car2 = Car.builder()
                .id(2L)
                .number("AA000A00")
                .build();
        List<Car> sampleCars = List.of(car, car2);
        when(carRepository.findAll()).thenReturn(sampleCars);

        //Act
        List<Car> cars = carService.getAll();

        //Assert
        assertEquals(sampleCars, cars, "Returned list should be the same");
    }

    @DisplayName("Get car by correct id")
    @Test
    public void testGetCarById_whenValidId_returnsCarObject() {
        //Arrange
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));

        //Act
        Car foundCar = carService.getById(car.getId());

        //Assert
        assertEquals(car, foundCar, "Returned car should be the same");
    }

    @DisplayName("Get car by not existing id")
    @Test
    public void testGetZoneById_whenNotValidId_throwsResourceNotFoundException() {
        //Arrange
        String expectedExceptionMessage = "Car not found";
        Long nonExistingId = 12L;
        when(carRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            carService.getById(nonExistingId);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(),
                "Exception error message is not correct");
    }

    @DisplayName("Get all cars by user id")
    @Test
    public void testGetCarsByUserId_whenUserHasCars_returnsCars() {
        //Arrange
        Car car2 = Car.builder()
                .id(2L)
                .number("AA000A00")
                .build();
        List<Car> sampleCars = List.of(car, car2);
        when(carRepository.findAllByUserId(anyLong())).thenReturn(sampleCars);

        //Act
        List<Car> foundCars = carService.getAllByUserId(anyLong());

        //Assert
        assertEquals(sampleCars, foundCars, "Returned list should be the same");
    }

    @DisplayName("Get all cars by user id when user has no cars")
    @Test
    public void testGetCarsByUserId_whenUserHasNoCars_throwsIllegalStateException() {
        //Arrange
        String expectedErrorMessage = "User hasn't registered any car";
        when(carRepository.findAllByUserId(anyLong())).thenReturn(Collections.emptyList());

        //Act
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            carService.getAllByUserId(anyLong());
        });

        //Assert
        assertEquals(expectedErrorMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Create car with valid number")
    @Test
    public void testCreateCar_whenUserHasNoCarsWithThisNumber_returnsCarObject() {
        //Arrange
        when(carRepository.findByNumber(car.getNumber())).thenReturn(Optional.empty());

        //Act
        Car createdCar = carService.create(car, anyLong());

        //Assert
        assertNotNull(createdCar, "Created car should not be empty");

        verify(carRepository).assignToUser(anyLong(), eq(car.getId()));
        verify(carRepository).save(car);
    }

    @DisplayName("Create car if user has car with the same number")
    @Test
    public void testCreateCar_whenUserHasCarWithThisNumber_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "Car already exists";
        Car creatingCar = Car.builder()
                .number("AA000A00")
                .build();
        when(carRepository.findByNumber(creatingCar.getNumber())).thenReturn(Optional.of(car));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            carService.create(creatingCar, anyLong());
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception message is not correct");
    }

    @DisplayName("Update car with valid details")
    @Test
    public void testUpdateCar_whenCarDetailsAreValid_returnsUpdatedCarObject() {
        //Arrange
        Car updatedCar = new Car();
        updatedCar.setId(car.getId());
        updatedCar.setNumber("UU000U00");

        when(carRepository.findByNumber(updatedCar.getNumber())).thenReturn(Optional.empty());
        when(carRepository.findById(updatedCar.getId())).thenReturn(Optional.of(car));

        when(carRepository.save(updatedCar)).thenReturn(updatedCar);

        //Act
        Car result = carService.update(updatedCar);

        //Assert
        verify(carRepository).findByNumber(updatedCar.getNumber());
        verify(carRepository).save(updatedCar);
        assertEquals(updatedCar, result, "Updated car should be equals to input car");
    }

    @DisplayName("Update car number to the same number")
    @Test
    public void testUpdateCar_whenNewNumberIsEqualsExistingNumber_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "Car has the same number";
        when(carRepository.findByNumber(car.getNumber())).thenReturn(Optional.of(car));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            carService.update(car);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Update non-existing car")
    @Test
    public void testUpdateCar_whenCarDoesNotExist_throwsResourceNotFoundException() {
        //Arrange
        String expectedErrorMessage = "Car does not exists";
        when(carRepository.findByNumber(car.getNumber())).thenReturn(Optional.empty());
        when(carRepository.findById(car.getId())).thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            carService.update(car);
        });

        //Assert
        assertEquals(expectedErrorMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Delete car with valid details")
    @Test
    public void testDeleteCar_whenCarDetailsAreValid_returnsNothing() {
        //Arrange
        when(reservationRepository.findByCarId(car.getId())).thenReturn(Optional.empty());

        //Act
        carService.delete(car.getId());

        //Assert
        verify(carRepository).deleteById(car.getId());
    }

    @DisplayName("Delete car with reservations")
    @Test
    public void testDeleteCar_whenCarHasReservations_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "Can not delete car, because car has reservations";
        when(reservationRepository.findByCarId(car.getId())).thenReturn(Optional.of(new Reservation()));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            carService.delete(car.getId());
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }
}
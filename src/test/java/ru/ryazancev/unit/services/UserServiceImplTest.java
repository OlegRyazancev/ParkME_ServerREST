package ru.ryazancev.unit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.user.Role;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;
import ru.ryazancev.parkingreservationsystem.repositories.UserRepository;
import ru.ryazancev.parkingreservationsystem.services.impl.ReservationServiceImpl;
import ru.ryazancev.parkingreservationsystem.services.impl.UserServiceImpl;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ReservationServiceImpl reservationService;

    @InjectMocks
    private UserServiceImpl userService;


    private User user;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .name("Name")
                .email("Email")
                .password("encodedPassword")
                .passwordConfirmation("encodedPassword")
                .build();
    }

    @DisplayName("Get all users")
    @Test
    public void testGetAll_returnsListOfUsers() {
        //Arrange
        User user2 = User.builder()
                .id(2L)
                .name("Name2")
                .email("Email2")
                .password("password2")
                .passwordConfirmation("password2")
                .build();

        List<User> sampleUsers = List.of(user, user2);

        when(userRepository.findAll()).thenReturn(sampleUsers);

        //Act
        List<User> users = userService.getAll();

        //Assert
        assertEquals(sampleUsers, users, "Returned list should be the same");
    }

    @DisplayName("Get user by id")
    @Test
    public void testGetUserById_whenValidId_returnsUserObject() {
        //Arrange
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        //Act
        User foundUser = userService.getById(user.getId());

        //Assert
        assertEquals(user, foundUser, "Returned user should be th same");
    }

    @DisplayName("Get user by non-existing id")
    @Test
    public void testGetUserById_whenNotValidId_throwsResourceNotFoundException() {
        //Arrange
        String expectedExceptionMessage = "User not found";
        Long nonExistingId = 12L;
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getById(nonExistingId);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Get user by username(email)")
    @Test
    public void testGetUserByUsername_whenValidUsername_returnsUserObject() {
        //Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));


        //Act
        User foundUser = userService.getByUsername(user.getEmail());

        //Assert
        assertEquals(user, foundUser, "Returned user should be the same");
    }

    @DisplayName("Get user by non-existing username")
    @Test
    public void testGetUserByUsername_whenNotValidUsername_throwsResourceNotFoundException() {
        //Arrange
        String expectedErrorMessage = "User not found";
        String nonExistingUsername = "nonExistingUsername";
        when(userRepository.findByEmail(nonExistingUsername)).thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getByUsername(nonExistingUsername);
        });

        //Assert
        assertEquals(expectedErrorMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Create user with valid details")
    @Test
    public void testCreateUser_whenUserDetailsAreValid_returnsUserObject() {
        //Arrange
        String encodedPassword = "encodedPassword";
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn(encodedPassword);

        //Act
        User createdUser = userService.create(user);

        //Assert
        assertNotNull(createdUser, "Created user should not be null");
        assertEquals(encodedPassword, createdUser.getPassword(), "Created user must have encoded password");
        assertEquals(Set.of(Role.ROLE_USER), createdUser.getRoles());

        verify(userRepository).save(user);
    }

    @DisplayName("Create user with existing email")
    @Test
    public void testCreateUser_whenUserEmailExists_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "User already exists";
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            userService.create(user);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Create user with different password and password confirmation")
    @Test
    public void testCreateUser_whenDifferentPasswordAndPasswordConfirmation_throwsIllegalStateException() {
        //Arrange
        user.setPasswordConfirmation("qwerty");
        String expectedExceptionMessage = "Password and password confirmation do not equals";
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            userService.create(user);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("If user is car owner")
    @Test
    public void testIsCarOwner_whenUserIsCarOwner_returnsTrue() {
        //Arrange
        Long carId = 12L;
        Long userId = user.getId();

        when(userRepository.isCarOwner(userId, carId)).thenReturn(true);

        //Act
        boolean result = userService.isCarOwner(userId, carId);

        //Assert
        assertTrue(result);

        verify(userRepository).isCarOwner(userId, carId);
    }

    @DisplayName("If user is not car owner")
    @Test
    public void testIsCarOwner_whenUserIsNotCarOwner_returnsFalse() {
        //Arrange
        Long carId = 12L;
        Long userId = user.getId();

        when(userRepository.isCarOwner(userId, carId)).thenReturn(false);

        //Act
        boolean result = userService.isCarOwner(userId, carId);

        //Assert
        assertFalse(result);

        verify(userRepository).isCarOwner(userId, carId);
    }

    @DisplayName("If user is reservation owner")
    @Test
    public void testIsCarOwner_whenUserIsReservationOwner_returnsTrue() {
        //Arrange
        Long reservationId = 12L;
        Long userId = user.getId();

        when(userRepository.isReservationOwner(userId, reservationId)).thenReturn(true);

        //Act
        boolean result = userService.isReservationOwner(userId, reservationId);

        //Assert
        assertTrue(result);

        verify(userRepository).isReservationOwner(userId, reservationId);
    }

    @DisplayName("If user is not reservation owner")
    @Test
    public void testIsCarOwner_whenUserIsNotReservationOwner_returnsFalse() {
        //Arrange
        Long reservationId = 12L;
        Long userId = user.getId();

        when(userRepository.isReservationOwner(userId, reservationId)).thenReturn(false);

        //Act
        boolean result = userService.isReservationOwner(userId, reservationId);

        //Assert
        assertFalse(result);

        verify(userRepository).isReservationOwner(userId, reservationId);
    }

    @DisplayName("Update user")
    @Test
    public void testUpdateUser_returnsUpdatedUserObject() {
        //Arrange
        User updatedUser = User.builder()
                .id(user.getId())
                .name("TestName")
                .email("TestEmail")
                .password("password")
                .build();

        when(passwordEncoder.encode(updatedUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.findById(updatedUser.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        //Act
        User result = userService.update(updatedUser);

        //Assert

        assertEquals(updatedUser.getName(), result.getName(), "Name should be updated");
        assertEquals(updatedUser.getEmail(), result.getEmail(), "Email should be updated");
        verify(userRepository).save(user);
    }

    @DisplayName("Delete user")
    @Test
    public void testDeleteUser_returnsNothing() {
        //Arrange

        Long userId = user.getId();
        user.setCars(List.of(
                Car.builder().id(1L).build(),
                Car.builder().id(2L).build())
        );
        user.setReservations(List.of(
                Reservation.builder().id(1L).build(),
                Reservation.builder().id(2L).build())
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        //Act
        userService.delete(userId);

        //Assert
        verify(reservationService, times(user.getReservations().size())).delete(anyLong());
        verify(carRepository).deleteAll(user.getCars());
        verify(userRepository).deleteById(user.getId());
    }

}
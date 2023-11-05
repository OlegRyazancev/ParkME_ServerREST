package ru.ryazancev.parkingreservationsystem.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ryazancev.config.IntegrationTestBase;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    private final Long userId = 1L;

    @DisplayName("Is car owner")
    @Test
    public void testIfUserIsCarOwner_returnsTrue() {
        //Arrange
        Long carId = 1L;

        //Act
        Boolean isCarOwner = userRepository.isCarOwner(userId, carId);

        //Assert
        assertTrue(isCarOwner);
    }


    @DisplayName("Is reservation owner")
    @Test
    public void testIfUserIsReservationOwner_returnsTrue() {
        //Arrange
        Long reservationId = 1L;

        //Act
        Boolean isReservationOwner = userRepository.isReservationOwner(userId, reservationId);

        //Assert
        assertTrue(isReservationOwner);
    }

}
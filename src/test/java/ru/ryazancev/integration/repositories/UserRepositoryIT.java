package ru.ryazancev.integration.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ryazancev.integration.BaseIT;
import ru.ryazancev.parkingreservationsystem.repositories.UserRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRepositoryIT extends BaseIT {

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
        Boolean isReservationOwner =
                userRepository.isReservationOwner(userId, reservationId);

        //Assert
        assertTrue(isReservationOwner);
    }

}
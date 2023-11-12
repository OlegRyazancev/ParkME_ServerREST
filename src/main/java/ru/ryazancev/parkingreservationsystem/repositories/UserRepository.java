package ru.ryazancev.parkingreservationsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.user.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query(value = """
            SELECT EXISTS(SELECT 1
                          FROM users_cars
                          WHERE user_id = :userId
                            AND car_id = :carId)
            """, nativeQuery = true)
    Boolean isCarOwner(@Param("userId") Long userId,
                       @Param("carId") Long carId);

    @Query(value = """
            SELECT EXISTS(SELECT 1
                          FROM users u
                                   LEFT JOIN reservations r ON u.id = r.user_id
                          WHERE u.id = :userId
                            AND r.id = :reservationId)
            """, nativeQuery = true)
    Boolean isReservationOwner(@Param("userId") Long userId,
                               @Param("reservationId") Long reservationId);
}


package ru.ryazancev.parkingreservationsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByNumber(String number);

    @Query(value = """
            SELECT * FROM cars c
            JOIN users_cars uc ON c.id = uc.car_id
            WHERE uc.user_id = :userId
            """, nativeQuery = true)
    List<Car> findAllByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT *
            FROM reservations r
                     LEFT JOIN cars c ON r.car_id = c.id
            WHERE c.id = :carId
            """, nativeQuery = true)
    Optional<Reservation> findReservationByCarId(@Param("carId") Long carId);

    @Modifying
    @Query(value = """
            INSERT INTO users_cars (user_id, car_id)
            VALUES (:userId, :carId)
            """, nativeQuery = true)
    void assignToUser(@Param("userId") Long userId, @Param("carId") Long carId);
}

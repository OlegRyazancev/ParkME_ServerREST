package ru.ryazancev.parkingreservationsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByUserId(@Param("userId") Long userId);

    Optional<Reservation> findByCarId(Long carId);

    @Modifying
    @Query(value = """
            DELETE
            FROM reservations r
            WHERE r.time_to < :currentTime
            """, nativeQuery = true)
    void deleteExpiredReservations(@Param("currentTime") LocalDateTime currentTime);


}

package ru.ryazancev.parkingreservationsystem.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.reservation.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByUserIdOrderByTimeFromDesc(Long userId);

    List<Reservation> findAllByCarId(Long carId);

    List<Reservation> findAllByPlaceId(Long placeId);

    @EntityGraph(attributePaths = {"id", "timeFrom", "status"})
    List<Reservation> findByTimeFromBeforeAndStatus(LocalDateTime currentTime,
                                                    ReservationStatus status);

    @EntityGraph(attributePaths = {"id", "timeFrom", "status"})
    List<Reservation> findByTimeToBeforeAndStatus(LocalDateTime currentTime,
                                                  ReservationStatus status);

    @Modifying
    @Query(value = """
            UPDATE reservations
            SET status = :status
            WHERE id = :reservationId
            """, nativeQuery = true)
    void updateStatus(@Param("reservationId") Long reservationId,
                      @Param("status") String status);
}

package ru.ryazancev.parkingreservationsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByUserId(
            @Param("userId") Long userId);

    @Query(value = """
            SELECT *
            FROM reservations
            WHERE time_from < now()""",
            nativeQuery = true)
    List<Reservation> findExpiredReservations();

    Optional<Reservation> findByCarId(Long carId);


}

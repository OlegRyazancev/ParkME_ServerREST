package ru.ryazancev.parkingreservationsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByUserIdOrderByTimeFromDesc(Long userId);

    Optional<Reservation> findByCarId(Long carId);

    List<Reservation> findAllByCarId(Long carId);

    List<Reservation> findAllByPlaceId(Long placeId);

}

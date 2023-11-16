package ru.ryazancev.parkingreservationsystem.web.security.filter.cleanup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.services.ReservationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationCleanUpFilterProvider {

    private final ReservationService reservationService;

    @Transactional
    public void clean() {
        List<Reservation> expiredReservations =
                reservationService.getExpiredReservations();
        reservationService.deleteExpiredReservations(expiredReservations);
    }
}

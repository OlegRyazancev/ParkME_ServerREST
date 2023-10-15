package ru.ryazancev.parkingreservationsystem.web.security.filter.cleanup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationCleanUpFilterProvider {

    private final ReservationRepository reservationRepository;

    @Transactional
    public void clean() {
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println(currentTime);
        reservationRepository.deleteExpiredReservations(currentTime);
    }
}

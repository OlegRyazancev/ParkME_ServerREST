package ru.ryazancev.parkingreservationsystem.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.models.reservation.ReservationStatus;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;
import ru.ryazancev.parkingreservationsystem.services.ReservationScheduler;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationSchedulerImpl implements ReservationScheduler {


    private final ReservationRepository reservationRepository;

    @Scheduled(cron = "0 * * * * *")
    @Override
    @Transactional
    public void activateReservations() {
        List<Reservation> startedReservations = reservationRepository
                .findByTimeFromBeforeAndStatus(LocalDateTime.now(),
                        ReservationStatus.PLANNED);
        startedReservations.forEach(reservation ->
                reservationRepository.updateStatus(reservation.getId(),
                        ReservationStatus.ACTIVE.name())
        );
    }

    @Scheduled(cron = "0 * * * * *")
    @Override
    @Transactional
    public void completeReservations() {
        List<Reservation> completedReservations = reservationRepository
                .findByTimeToBeforeAndStatus(LocalDateTime.now(),
                        ReservationStatus.ACTIVE);
        completedReservations.forEach(reservation ->
                reservationRepository.updateStatus(reservation.getId(),
                        ReservationStatus.COMPLETED.name()));
    }
}

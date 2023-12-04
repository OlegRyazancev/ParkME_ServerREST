package ru.ryazancev.parkingreservationsystem.models.reservation;

import jakarta.persistence.*;
import lombok.*;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.models.user.User;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "reservations")
public class Reservation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time_from")
    private LocalDateTime timeFrom;

    @Column(name = "time_to")
    private LocalDateTime timeTo;

    @Column(name = "status")
    private ReservationStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "zone_id",
            referencedColumnName = "id")
    private Zone zone;

    @OneToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @OneToOne
    @JoinColumn(name = "car_id")
    private Car car;

    public Reservation(final Long id,
                       final LocalDateTime timeFrom,
                       final LocalDateTime timeTo,
                       final ReservationStatus status) {
        this.id = id;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.status = status;
    }
}

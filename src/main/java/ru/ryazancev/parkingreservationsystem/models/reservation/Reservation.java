package ru.ryazancev.parkingreservationsystem.models.reservation;

import lombok.Data;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.models.user.User;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Reservation implements Serializable {

    private Long id;
    private LocalDateTime timeFrom;
    private LocalDateTime timeTo;
    private User user;
    private Zone zone;
    private Place place;
    private Car car;
}

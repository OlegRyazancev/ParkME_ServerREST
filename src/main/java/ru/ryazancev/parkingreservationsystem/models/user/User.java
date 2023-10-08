package ru.ryazancev.parkingreservationsystem.models.user;

import lombok.Data;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class User implements Serializable {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String passwordConfirmation;
    private Set<Role> roles;
    private List<Car> cars;
    private List<Reservation> reservations;
}

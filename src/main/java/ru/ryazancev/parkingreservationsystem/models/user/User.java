package ru.ryazancev.parkingreservationsystem.models.user;

import jakarta.persistence.*;
import lombok.*;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Transient
    private String passwordConfirmation;


    @CollectionTable(name = "users_roles")
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(value = EnumType.STRING)
    private Set<Role> roles;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_cars",
            inverseJoinColumns = @JoinColumn(name = "car_id"))
    private List<Car> cars;

    @OneToMany
    @JoinTable(name = "reservations",
            inverseJoinColumns = @JoinColumn(name = "id"))
    private List<Reservation> reservations;
}

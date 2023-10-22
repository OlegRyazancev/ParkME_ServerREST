package ru.ryazancev.parkingreservationsystem.models.parking;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "zones")
public class Zone implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private Integer number;

    @OneToMany
    @JoinTable(name = "zones_places",
            inverseJoinColumns = @JoinColumn(name = "place_id"))
    private List<Place> places;
}

package ru.ryazancev.parkingreservationsystem.models.parking;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "zones")
@Data
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

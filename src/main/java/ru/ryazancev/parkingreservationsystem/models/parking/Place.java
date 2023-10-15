package ru.ryazancev.parkingreservationsystem.models.parking;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "places")
@Data
public class Place implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private Integer number;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private Status status;
}

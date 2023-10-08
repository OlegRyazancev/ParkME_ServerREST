package ru.ryazancev.parkingreservationsystem.models.parking;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class Zone implements Serializable {

    private Long id;
    private Integer number;
    private Integer freePlaces;
}

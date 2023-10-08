package ru.ryazancev.parkingreservationsystem.models.parking;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class Zone {

    private Long id;
    private Integer number;
    private List<Place> places;
}

package ru.ryazancev.parkingreservationsystem.models.parking;

import lombok.Data;

@Data
public class Place {

    private Long id;
    private Integer number;
    private Status status;
    private Zone zone;
}

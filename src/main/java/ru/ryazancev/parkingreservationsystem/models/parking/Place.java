package ru.ryazancev.parkingreservationsystem.models.parking;

import lombok.Data;

import java.io.Serializable;

@Data
public class Place implements Serializable {

    private Long id;
    private Integer number;
    private Status status;
}

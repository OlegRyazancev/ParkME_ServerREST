package ru.ryazancev.parkingreservationsystem.models.car;

import lombok.Data;

import java.io.Serializable;

@Data
public class Car implements Serializable {

    private Long id;
    private String number;
}

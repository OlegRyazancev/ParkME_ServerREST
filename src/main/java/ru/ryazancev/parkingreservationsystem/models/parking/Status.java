package ru.ryazancev.parkingreservationsystem.models.parking;


import java.io.Serializable;

public enum Status implements Serializable {
    FREE,
    OCCUPIED,
    DISABLE
}

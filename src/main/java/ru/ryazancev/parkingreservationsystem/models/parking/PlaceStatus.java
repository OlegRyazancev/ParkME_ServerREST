package ru.ryazancev.parkingreservationsystem.models.parking;


import java.io.Serializable;

public enum PlaceStatus implements Serializable {
    FREE,
    OCCUPIED,
    DISABLE
}

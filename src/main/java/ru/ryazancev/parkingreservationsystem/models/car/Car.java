package ru.ryazancev.parkingreservationsystem.models.car;

import lombok.Data;
import ru.ryazancev.parkingreservationsystem.models.user.User;

@Data
public class Car {

    private Long id;
    private String number;
    private User user;
}

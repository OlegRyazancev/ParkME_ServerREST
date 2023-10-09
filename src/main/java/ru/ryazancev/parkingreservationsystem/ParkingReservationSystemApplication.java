package ru.ryazancev.parkingreservationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.web.dto.PlaceDTO;
import ru.ryazancev.parkingreservationsystem.web.mappers.PlaceMapper;

import java.util.Optional;

@SpringBootApplication
public class ParkingReservationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkingReservationSystemApplication.class, args);
    }

}

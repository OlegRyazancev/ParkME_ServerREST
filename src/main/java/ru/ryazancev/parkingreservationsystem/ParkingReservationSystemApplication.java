package ru.ryazancev.parkingreservationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ParkingReservationSystemApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ParkingReservationSystemApplication.class, args);
    }

}

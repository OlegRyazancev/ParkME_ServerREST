package ru.ryazancev.parkingreservationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.FilterChainProxy;

import java.time.LocalDateTime;

@SpringBootApplication
public class ParkingReservationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkingReservationSystemApplication.class, args);
    }

}

package ru.ryazancev.parkingreservationsystem.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.web.dto.validation.OnCreate;
import ru.ryazancev.parkingreservationsystem.web.dto.validation.OnUpdate;

import java.time.LocalDateTime;

@Data
public class ReservationDTO {

    @NotNull(message = "Id must be not null", groups = OnUpdate.class)
    private Long id;

    @NotNull(message = "Id must be not null", groups = {OnCreate.class})
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timeFrom;

    @NotNull(message = "Id must be not null", groups = {OnCreate.class,OnUpdate.class})
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timeTo;

    @NotNull(message = "Zone must not be null", groups = OnCreate.class)
    private Zone zone;

    @NotNull(message = "Car must not be null", groups = OnCreate.class)
    private Place place;

    @NotNull(message = "Car must not be null", groups = OnCreate.class)
    private Car car;


}

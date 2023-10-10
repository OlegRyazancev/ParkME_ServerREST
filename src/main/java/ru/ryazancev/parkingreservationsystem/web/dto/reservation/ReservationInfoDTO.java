package ru.ryazancev.parkingreservationsystem.web.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.ryazancev.parkingreservationsystem.web.dto.car.CarDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.place.PlaceInfoDTO;
import ru.ryazancev.parkingreservationsystem.util.validation.OnCreate;
import ru.ryazancev.parkingreservationsystem.util.validation.OnUpdate;
import ru.ryazancev.parkingreservationsystem.web.dto.zone.ZoneInfoDTO;

import java.time.LocalDateTime;

@Data
public class ReservationInfoDTO {
    @NotNull(message = "Id must be not null", groups = OnUpdate.class)
    private Long id;

    @NotNull(message = "Id must be not null", groups = {OnCreate.class})
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timeFrom;

    @NotNull(message = "Id must be not null", groups = {OnCreate.class, OnUpdate.class})
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timeTo;

    @NotNull(message = "Zone must not be null", groups = OnCreate.class)
    private ZoneInfoDTO zone;

    @NotNull(message = "Car must not be null", groups = OnCreate.class)
    private PlaceInfoDTO place;

    @NotNull(message = "Car must not be null", groups = OnCreate.class)
    private CarDTO car;
}

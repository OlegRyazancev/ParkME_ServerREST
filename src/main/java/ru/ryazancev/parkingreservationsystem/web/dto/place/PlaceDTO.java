package ru.ryazancev.parkingreservationsystem.web.dto.place;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.web.dto.validation.OnCreate;
import ru.ryazancev.parkingreservationsystem.web.dto.validation.OnUpdate;

@Data
public class PlaceDTO {

    @NotNull(message = "Id must be not null", groups = OnUpdate.class)
    private Long id;

    @NotNull(message = "Number must be not null", groups = {OnCreate.class, OnUpdate.class})
    @Min(value = 1, message = "Number should be greater than 1", groups = {OnCreate.class, OnUpdate.class})
    private Integer number;

    private Status status;

}

package ru.ryazancev.parkingreservationsystem.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.ryazancev.parkingreservationsystem.web.dto.validation.OnCreate;
import ru.ryazancev.parkingreservationsystem.web.dto.validation.OnUpdate;


@Data
public class ZoneDTO {

    @NotNull(message = "Id must not be null", groups = OnUpdate.class)
    private Long id;

    @NotNull(message = "Number must not be null", groups = {OnCreate.class, OnUpdate.class})
    @Min(value = 1, message = "Number should be greater than 1", groups = {OnCreate.class, OnUpdate.class})
    private Integer number;

    @NotNull(message = "Free places must be not null", groups = OnCreate.class)
    private Integer freePlaces;

}

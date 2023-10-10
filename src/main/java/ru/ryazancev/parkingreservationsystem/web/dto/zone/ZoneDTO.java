package ru.ryazancev.parkingreservationsystem.web.dto.zone;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.ryazancev.parkingreservationsystem.util.validation.OnCreate;
import ru.ryazancev.parkingreservationsystem.util.validation.OnUpdate;


@Data
public class ZoneDTO {

    @NotNull(message = "Id must not be null", groups = OnUpdate.class)
    private Long id;

    @NotNull(message = "Number must not be null", groups = {OnCreate.class, OnUpdate.class})
    @Min(value = 1, message = "Number should be greater than 1", groups = {OnCreate.class, OnUpdate.class})
    private Integer number;

    private Integer freePlaces;

}

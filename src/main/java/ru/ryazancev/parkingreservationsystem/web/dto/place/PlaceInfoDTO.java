package ru.ryazancev.parkingreservationsystem.web.dto.place;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.ryazancev.parkingreservationsystem.util.validation.OnCreate;
import ru.ryazancev.parkingreservationsystem.util.validation.OnUpdate;

@Data
public class PlaceInfoDTO {
    @NotNull(message = "Id must be not null", groups = OnUpdate.class)
    private Long id;

    @NotNull(message = "Number must be not null", groups = {OnCreate.class, OnUpdate.class})
    @Min(value = 1, message = "Number should be greater than 1", groups = {OnCreate.class, OnUpdate.class})
    private Integer number;
}

package ru.ryazancev.parkingreservationsystem.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.services.PlaceService;
import ru.ryazancev.parkingreservationsystem.web.dto.PlaceDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.validation.OnUpdate;
import ru.ryazancev.parkingreservationsystem.web.mappers.PlaceMapper;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final PlaceMapper placeMapper;

    @GetMapping("/{id}")
    public PlaceDTO getById(@PathVariable("id") Long id) {
        Place place = placeService.getById(id);
        return placeMapper.toDTO(place);
    }

    @PutMapping("/{id}")
    public PlaceDTO makeDisableById(@PathVariable("id") Long id) {

        Place disabledPlace = placeService.makeDisable(id);

        return placeMapper.toDTO(disabledPlace);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        placeService.delete(id);
    }
}

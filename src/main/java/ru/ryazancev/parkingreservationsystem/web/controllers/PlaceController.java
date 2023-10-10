package ru.ryazancev.parkingreservationsystem.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.services.PlaceService;
import ru.ryazancev.parkingreservationsystem.web.dto.place.PlaceDTO;
import ru.ryazancev.parkingreservationsystem.web.mappers.place.PlaceMapper;

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

    @PutMapping("/{id}/status")
    public PlaceDTO changeStatusById(@PathVariable("id") Long id, @RequestParam String status) {

        Place disabledPlace = placeService.changeStatus(id, Status.valueOf(status));

        return placeMapper.toDTO(disabledPlace);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        placeService.delete(id);
    }
}

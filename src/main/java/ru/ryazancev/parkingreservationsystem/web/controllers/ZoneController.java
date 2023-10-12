package ru.ryazancev.parkingreservationsystem.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.services.PlaceService;
import ru.ryazancev.parkingreservationsystem.services.ZoneService;
import ru.ryazancev.parkingreservationsystem.web.dto.place.PlaceDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.zone.ZoneDTO;
import ru.ryazancev.parkingreservationsystem.util.validation.OnCreate;
import ru.ryazancev.parkingreservationsystem.util.validation.OnUpdate;
import ru.ryazancev.parkingreservationsystem.util.mappers.place.PlaceMapper;
import ru.ryazancev.parkingreservationsystem.util.mappers.zone.ZoneMapper;

import java.util.List;

@RestController
@RequestMapping("api/v1/zones")
@RequiredArgsConstructor
@Validated
public class ZoneController {

    private final ZoneService zoneService;
    private final PlaceService placeService;

    private final ZoneMapper zoneMapper;
    private final PlaceMapper placeMapper;


    @GetMapping
    public List<ZoneDTO> getZones() {
        List<Zone> zones = zoneService.getAll();
        return zoneMapper.toDTO(zones);
    }

    @GetMapping("/{id}")
    public ZoneDTO getById(@PathVariable("id") Long id) {
        Zone zone = zoneService.getById(id);

        return zoneMapper.toDTO(zone);
    }

    @GetMapping("{id}/places")
    public List<PlaceDTO> getPlacesByZoneId(@PathVariable("id") Long id) {
        List<Place> places = placeService.getAllByZoneId(id);

        return placeMapper.toDTO(places);
    }
}

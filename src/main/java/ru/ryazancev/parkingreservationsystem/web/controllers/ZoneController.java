package ru.ryazancev.parkingreservationsystem.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.services.PlaceService;
import ru.ryazancev.parkingreservationsystem.services.ZoneService;
import ru.ryazancev.parkingreservationsystem.web.dto.PlaceDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.ZoneDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.validation.OnCreate;
import ru.ryazancev.parkingreservationsystem.web.dto.validation.OnUpdate;
import ru.ryazancev.parkingreservationsystem.web.mappers.PlaceMapper;
import ru.ryazancev.parkingreservationsystem.web.mappers.ZoneMapper;

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

    @PostMapping("{id}/places")
    public PlaceDTO createPlace(@PathVariable Long id, @Validated(OnCreate.class) @RequestBody PlaceDTO placeDTO) {
        Place place = placeMapper.toEntity(placeDTO);
        Place createdPlace = placeService.create(place, id);

        return placeMapper.toDTO(createdPlace);
    }

    @PostMapping
    public ZoneDTO create(@Validated(OnCreate.class) @RequestBody ZoneDTO zoneDTO) {
        Zone zone = zoneMapper.toEntity(zoneDTO);
        Zone createdZone = zoneService.create(zone);

        return zoneMapper.toDTO(createdZone);
    }

    @PutMapping
    public ZoneDTO update(@Validated(OnUpdate.class) @RequestBody ZoneDTO zoneDTO) {
        Zone zone = zoneMapper.toEntity(zoneDTO);
        Zone updatedZone = zoneService.update(zone);

        return zoneMapper.toDTO(updatedZone);
    }

    @DeleteMapping("/{id}")
    public void deleteZoneAndAssociatedPlaces(@PathVariable("id") Long id) {
        zoneService.delete(id);
    }
}

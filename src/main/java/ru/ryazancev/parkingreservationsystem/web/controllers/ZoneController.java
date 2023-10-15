package ru.ryazancev.parkingreservationsystem.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.services.PlaceService;
import ru.ryazancev.parkingreservationsystem.services.ZoneService;
import ru.ryazancev.parkingreservationsystem.util.mappers.place.PlaceMapper;
import ru.ryazancev.parkingreservationsystem.util.mappers.zone.ZoneMapper;
import ru.ryazancev.parkingreservationsystem.web.dto.place.PlaceDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.zone.ZoneDTO;

import java.util.List;

@RestController
@RequestMapping("api/v1/zones")
@RequiredArgsConstructor
@Validated
@Tag(name = "Zone Controller", description = "Zone API")
public class ZoneController {

    private final ZoneService zoneService;
    private final PlaceService placeService;

    private final ZoneMapper zoneMapper;
    private final PlaceMapper placeMapper;


    @GetMapping
    @Operation(summary = "Get zones")
    public List<ZoneDTO> getZones() {
        List<Zone> zones = zoneService.getAll();
        return zoneMapper.toDTO(zones);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get zone by id")
    public ZoneDTO getById(@PathVariable("id") Long id) {
        Zone zone = zoneService.getById(id);

        return zoneMapper.toDTO(zone);
    }

    @GetMapping("{id}/places")
    @Operation(summary = "Get places by zone id")
    public List<PlaceDTO> getPlacesByZoneId(@PathVariable("id") Long id) {
        List<Place> places = placeService.getAllByZoneId(id);

        return placeMapper.toDTO(places);
    }

    @GetMapping("/{id}/places/free")
    @Operation(summary = "Get free places by zone id")
    public List<PlaceDTO> getFreePlaces(@PathVariable("id") Long id) {
        List<Place> places = placeService.getFreePlacesByZoneId(id);

        return placeMapper.toDTO(places);
    }
}

package ru.ryazancev.parkingreservationsystem.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.services.PlaceService;
import ru.ryazancev.parkingreservationsystem.services.ZoneService;
import ru.ryazancev.parkingreservationsystem.util.mappers.ZoneInfoMapper;
import ru.ryazancev.parkingreservationsystem.util.mappers.ZoneMapper;
import ru.ryazancev.parkingreservationsystem.web.dto.zone.ZoneDTO;
import ru.ryazancev.parkingreservationsystem.web.dto.zone.ZoneInfoDTO;

import java.util.List;

@RestController
@RequestMapping("api/v1/zones")
@RequiredArgsConstructor
@Validated
@Tag(name = "Zone Controller", description = "Zone API")
public class ZoneController {

    private final ZoneService zoneService;
    private final PlaceService placeService;
    private final ZoneInfoMapper zoneReadMapper;
    private final ZoneMapper zoneMapper;
//    private final PlaceMapper placeMapper;

    @GetMapping
    @QueryMapping(name = "zones")
    @Operation(summary = "Get zones")
    public List<ZoneDTO> getZones() {
        List<Zone> zones = zoneService.getAll();
        List<ZoneDTO> zoneDTO = zoneMapper.toDTO(zones);
        zoneDTO.forEach(zone -> {
            zone.setTotalPlaces(
                    placeService.countAllPlacesByZoneID(zone.getId()));
            zone.setFreePlaces(
                    placeService.countFreePlacesByZoneID(zone.getId()));
        });

        return zoneDTO;
    }

    @GetMapping("/{id}")
    @QueryMapping("zoneById")
    @Operation(summary = "Get zone by id")
    public ZoneInfoDTO getById(@PathVariable("id")
                               @Argument final Long zoneId) {
        Zone zone = zoneService.getById(zoneId);

        return zoneReadMapper.toDTO(zone);
    }
}

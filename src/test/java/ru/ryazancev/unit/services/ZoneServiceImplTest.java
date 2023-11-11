package ru.ryazancev.unit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.repositories.ZoneRepository;
import ru.ryazancev.parkingreservationsystem.services.impl.ZoneServiceImpl;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;


@ExtendWith(MockitoExtension.class)
public class ZoneServiceImplTest {

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private ZoneServiceImpl zoneService;

    private Zone zone;
    private List<Place> associatedPlaces;

    @BeforeEach
    public void setup() {
        zone = Zone.builder()
                .id(1L)
                .number(1)
                .build();
        associatedPlaces = List.of(
                new Place(1L, 1, Status.FREE),
                new Place(2L, 2, Status.FREE),
                new Place(3L, 3, Status.DISABLE));
        zone.setPlaces(associatedPlaces);
    }

    @DisplayName("Get all zones")
    @Test
    public void testGetAllZones_returnsListOfZones() {
        //Arrange
        Zone zone2 = Zone.builder()
                .id(2L)
                .number(2)
                .places(List.of(new Place()))
                .build();
        List<Zone> sampleZones = List.of(zone, zone2);
        when(zoneRepository.findAll()).thenReturn(sampleZones);

        //Act
        List<Zone> zones = zoneService.getAll();

        //Assert
        assertEquals(sampleZones.size(), zones.size(), "Returned list should have the same size");
    }


    @DisplayName("Get zone by correct id")
    @Test
    public void testGetZoneById_whenValidId_returnsZoneObject() {
        //Arrange
        when(zoneRepository.findById(zone.getId())).thenReturn(Optional.of(zone));

        //Act
        Zone foundZone = zoneService.getById(zone.getId());

        //Assert
        assertEquals(zone, foundZone, "Returned zone should be the same");
    }

    @DisplayName("Get zone by not existing id")
    @Test
    public void testGetZoneById_whenNotValidId_throwsResourceNotFoundException() {
        //Arrange
        String expectedExceptionMessage = "Zone not found";
        Long nonExistingId = 12L;
        when(zoneRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            zoneService.getById(nonExistingId);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(),
                "Exception error message is not correct");
    }

    @DisplayName("Create zone with valid number")
    @Test
    public void testCreateZone_whenValidNumber_returnsZoneObject() {
        //Arrange
        when(zoneRepository.findByNumber(zone.getNumber())).thenReturn(Optional.empty());

        //Act
        Zone createdZone = zoneService.create(zone);

        //Assert
        assertNotNull(createdZone, "Created zone should not be empty");

        verify(zoneRepository).save(zone);
    }

    @DisplayName("Create zone with existing number")
    @Test
    public void testCreateZone_whenNumberExists_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "Zone is already exists";
        Zone creatingZone = Zone.builder()
                .number(1)
                .build();

        when(zoneRepository.findByNumber(creatingZone.getNumber())).thenReturn(Optional.of(zone));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            zoneService.create(creatingZone);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Update zone with valid details")
    @Test
    public void testUpdateZone_whenZoneDetailsAreValid_returnsUpdatedZoneObject() {
        //Arrange (Given)
        Zone updatedZone = new Zone();
        updatedZone.setId(zone.getId());
        updatedZone.setNumber(213);

        when(zoneRepository.findByNumber(updatedZone.getNumber())).thenReturn(Optional.empty());

        //Act (When)
        Zone result = zoneService.update(updatedZone);

        //Assert (Then)
        verify(zoneRepository).findByNumber(updatedZone.getNumber());
        verify(zoneRepository).save(updatedZone);

        assertEquals(updatedZone, result, "UpdatedZone should be equals inputZone ");
    }

    @DisplayName("Update zone number to the same number")
    @Test
    public void testUpdateZone_whenNewNumberIsEqualsExistingNumber_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "Zone is already exists";
        when(zoneRepository.findByNumber(zone.getNumber())).thenReturn(Optional.of(zone));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            zoneService.update(zone);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Delete zone with valid details")
    @Test
    public void testDeleteZone_whenZoneDetailsAreValid_returnsNothing() {
        //Arrange
        when(zoneRepository.findById(zone.getId())).thenReturn(Optional.of(zone));

        //Act
        zoneService.delete(zone.getId());

        //Assert
        verify(zoneRepository).findById(zone.getId());
        associatedPlaces.forEach(place -> {
            verify(placeRepository).deleteById(place.getId());
        });
        verify(zoneRepository).deleteById(zone.getId());
    }

    @DisplayName("Delete zone with occupied places")
    @Test
    public void testDeleteZone_whenZoneHasOccupiedPlaces_throwsIllegalStateException() {
        //Arrange
        zone.getPlaces().get(1).setStatus(Status.OCCUPIED);
        String expectedExceptionMessage = "Zone have occupied places";
        when(zoneRepository.findById(zone.getId())).thenReturn(Optional.of(zone));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            zoneService.delete(zone.getId());
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Delete non existing zone")
    @Test
    public void testDeleteZone_whenZoneDoesNotExist_throwsResourceNotFoundException() {
        //Arrange
        String expectedExceptionMessage = "Zone not found";
        when(zoneRepository.findById(zone.getId())).thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            zoneService.delete(zone.getId());
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

}
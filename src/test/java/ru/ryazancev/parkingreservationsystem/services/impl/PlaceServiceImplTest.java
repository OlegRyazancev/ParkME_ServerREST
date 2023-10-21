package ru.ryazancev.parkingreservationsystem.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class PlaceServiceImplTest {

    @MockBean
    private PlaceRepository placeRepository;

    @Autowired
    private PlaceServiceImpl placeService;

    private Place place;

    private Long zoneId;

    @BeforeEach
    public void setup() {
        place = Place.builder()
                .id(1L)
                .number(1)
                .status(Status.FREE)
                .build();
        zoneId = 1L;
    }


    @DisplayName("Get place by correct id")
    @Test
    public void testGetPlaceById_whenValidId_returnsPlaceObject() {

        //Arrange
        when(placeRepository.findById(place.getId())).thenReturn(Optional.of(place));

        //Act
        Place foundPlace = placeService.getById(place.getId());

        //Assert
        verify(placeRepository).findById(place.getId());
        assertEquals(place, foundPlace);
    }


    @DisplayName("Get place by not existing id")
    @Test
    public void testGetPlaceById_whenNotValidId_throwsResourceNotFoundException() {

        //Arrange
        String expectedExceptionMessage = "Place not found";
        Long nonExistingId = 12L;
        when(placeRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            placeService.getById(nonExistingId);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(),
                "Exception error message is not correct");
    }

    @DisplayName("Get free places by zone Id")
    @Test
    public void testGetFreePlacesByZoneId_whenValidZoneId_returnsListOfFreePlaces() {
        //Arrange
        List<Place> places = Arrays.asList(
                new Place(1L, 1, Status.FREE),
                new Place(2L, 2, Status.OCCUPIED),
                new Place(3L, 3, Status.FREE)
        );

        when(placeRepository.findAllByZoneId(zoneId)).thenReturn(places);

        //Act
        List<Place> freePlaces = placeService.getFreePlacesByZoneId(zoneId);

        //Assert
        assertEquals(2, freePlaces.size(), "Both lists of places should have the same size");
    }

    @DisplayName("Get all places by zoneId")
    @Test
    public void testGetAllPlacesByZoneId_whenValidZoneId_returnsListOfPlaces() {
        //Arrange
        List<Place> places = Arrays.asList(
                new Place(1L, 1, Status.FREE),
                new Place(2L, 2, Status.OCCUPIED),
                new Place(3L, 3, Status.FREE),
                new Place(4L, 4, Status.DISABLE)
        );

        when(placeRepository.findAllByZoneId(zoneId)).thenReturn(places);

        //Act
        List<Place> allPlaces = placeService.getAllByZoneId(zoneId);

        //Assert
        assertEquals(places, allPlaces, "Lists of places should match");
    }

    @DisplayName("Create place with valid number")
    @Test
    public void testCreatePlaceInZone_whenValidNumber_returnsPlaceObject() {
        //Arrange
        place.setId(null);
        when(placeRepository.findAllByZoneId(zoneId)).thenReturn(Collections.emptyList());
        when(placeRepository.save(place)).thenReturn(new Place(1L, place.getNumber(), place.getStatus()));

        //Act
        Place createdPlace = placeService.create(place, zoneId);

        //Assert
        assertEquals(place, createdPlace, "Created place should match the input place");
        verify(placeRepository).save(place);
        verify(placeRepository).assignToZone(place.getId(), zoneId);
    }

    @DisplayName("Create place with existing number in zone")
    @Test
    public void testCreatePlaceInZone_whenNumberExists_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "Place is already exists in this zone";
        Place extistingPlace = Place.builder()
                .id(2L)
                .number(1)
                .status(Status.DISABLE)
                .build();

        when(placeRepository.findAllByZoneId(zoneId)).thenReturn(Collections.singletonList(extistingPlace));

        //Act&&Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            placeService.create(place, zoneId);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Change place status with valid details")
    @Test
    public void testChangePlaceStatus_whenPlaceDetailsAreValid_shouldReturnPlaceObject() {
        //Arrange
        Status status = Status.DISABLE;

        when(placeRepository.findById(place.getId())).thenReturn(Optional.of(place));

        //Act
        Place placeWithChangedStatus = placeService.changeStatus(place.getId(), status);

        //Assert
        assertEquals(place, placeWithChangedStatus, "Place with changed status should be equals to input place");
    }

    @DisplayName("Change place status to OCCUPIED status")
    @Test
    public void testChangeStatus_whenChangingStatusIsOccupied_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "Can not use OCCUPIED status here";
        Status status = Status.OCCUPIED;

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            placeService.changeStatus(place.getId(), status);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Change status on not existing place")
    @Test
    public void testChangeStatus_whenPlaceNotExists_throwsResourceNotFoundException() {
        //Arrange
        String expectedExceptionMessage = "Place not found";
        Status status = Status.DISABLE;
        when(placeRepository.findById(place.getId())).thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            placeService.changeStatus(place.getId(), status);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Change place status to the same status")
    @Test
    public void testChangeStatus_whenChangingStatusEqualActualPlaceStatus_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "Place already has this status";
        Status status = Status.FREE;

        when(placeRepository.findById(place.getId())).thenReturn(Optional.of(place));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            placeService.changeStatus(place.getId(), status);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Change place status on place with occupied status")
    @Test
    public void testChangeStatus_whenPlaceStatusIsOccupied_thenThrowsIllegalStateException() {
        //Arrange
        place.setStatus(Status.OCCUPIED);
        String expectedExceptionMessage = "Can not change status, because place is occupied";
        Status status = Status.FREE;

        when(placeRepository.findById(place.getId())).thenReturn(Optional.of(place));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            placeService.changeStatus(place.getId(), status);
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Delete place with valid details")
    @Test
    public void testDeletePlace_whenPlaceDetailsIsValid_returnsNothing() {
        //Arrange
        when(placeRepository.findById(place.getId())).thenReturn(Optional.of(place));
        willDoNothing().given(placeRepository).deleteById(place.getId());

        //Act
        placeService.delete(place.getId());

        //Assert
        verify(placeRepository).deleteById(place.getId());
    }

    @DisplayName("Delete non existing place")
    @Test
    public void testDeletePlace_whenPlaceNotExists_throwsResourceNotFoundException() {
        //Arrange
        String expectedExceptionMessage = "Place not found";
        when(placeRepository.findById(place.getId())).thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            placeService.delete(place.getId());
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

    @DisplayName("Delete place with occupied status throws IllegalStateException")
    @Test
    public void testDeletePlace_whenPlaceStatusIsOccupied_throwsIllegalStateException() {
        //Arrange
        place.setStatus(Status.OCCUPIED);
        String expectedExceptionMessage = "Can not delete occupied place";

        when(placeRepository.findById(place.getId())).thenReturn(Optional.of(place));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            placeService.delete(place.getId());
        });

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(), "Exception error message is not correct");
    }

}
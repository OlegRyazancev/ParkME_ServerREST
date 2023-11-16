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
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.services.impl.PlaceServiceImpl;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class PlaceServiceImplTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceServiceImpl placeService;

    private Place place;

    @BeforeEach
    public void setup() {
        place = Place.builder()
                .id(1L)
                .number(1)
                .status(Status.FREE)
                .build();
    }


    @DisplayName("Get place by correct id")
    @Test
    public void testGetPlaceById_whenValidId_returnsPlaceObject() {

        //Arrange
        when(placeRepository.findById(place.getId()))
                .thenReturn(Optional.of(place));

        //Act
        Place foundPlace = placeService.getById(place.getId());

        //Assert
        assertEquals(place, foundPlace,
                "Returned place should be the same");
    }


    @DisplayName("Get place by not existing id")
    @Test
    public void testGetPlaceById_whenNotValidId_throwsResourceNotFoundException() {

        //Arrange
        String expectedExceptionMessage = "Place not found";
        Long nonExistingId = 12L;
        when(placeRepository.findById(nonExistingId))
                .thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                placeService.getById(nonExistingId));

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(),
                "Exception error message is not correct");
    }


    @DisplayName("Create places in zone")
    @Test
    public void testCreatePlacesInZone_whenValidNumberOfCreatingPlaces_returnsListOfCreatedPlaces() {
        // Arrange
        Long zoneId = 1L;
        List<Place> places = List.of(
                Place.builder().number(1).build(),
                Place.builder().number(2).build()
        );
        Place lastExistPlace = places.get(places.size() - 1);

        int numberOfPlaces = 5;

        when(placeRepository.findAllByZoneId(zoneId))
                .thenReturn(places);
        when(placeRepository.save(any(Place.class)))
                .thenAnswer(invocation -> {
                    Place place = invocation.getArgument(0);
                    if (place.getId() == null)
                        place.setId(1000L);
                    return place;
                });

        // Act
        List<Place> createdPlaces = placeService.createPlacesInZone(zoneId, numberOfPlaces);

        // Assert
        assertEquals(numberOfPlaces, createdPlaces.size());

        Place firstCreatedPlace = createdPlaces.get(0);
        assertEquals(lastExistPlace.getNumber() + 1, firstCreatedPlace.getNumber());

        createdPlaces.forEach(p ->
                assertEquals(Status.FREE, p.getStatus()));

        verify(placeRepository, times(numberOfPlaces)).save(any(Place.class));
        verify(placeRepository, times(numberOfPlaces)).assignToZone(anyLong(), anyLong());
    }

    @DisplayName("Create places in zone")
    @Test
    public void testCreatePlacesInZone_whenNotValidNumberOfCreatingPlaces_returnsListOfCreatedPlaces() {
        //Arrange
        String expectedExceptionMessage = "Number of places must be a positive integer";
        Long zoneId = 1L;
        //Act && Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                placeService.createPlacesInZone(zoneId, 0));

        //Assert

        assertEquals(expectedExceptionMessage, thrown.getMessage(),
                "Exception error message is not correct");
    }


    @DisplayName("Change place status with valid details")
    @Test
    public void testChangePlaceStatus_whenPlaceDetailsAreValid_shouldReturnPlaceObject() {
        //Arrange
        Status status = Status.DISABLE;

        when(placeRepository.findById(place.getId()))
                .thenReturn(Optional.of(place));
        when(placeRepository.save(place))
                .thenReturn(place);

        //Act
        Place placeWithChangedStatus = placeService.changeStatus(place.getId(), status);

        //Assert
        assertEquals(status, placeWithChangedStatus.getStatus(),
                "Place status should be changed");
        verify(placeRepository).save(place);
    }

    @DisplayName("Change place status to OCCUPIED status")
    @Test
    public void testChangeStatus_whenChangingStatusIsOccupied_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "Can not use OCCUPIED status here";
        Status status = Status.OCCUPIED;

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
                placeService.changeStatus(place.getId(), status));

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(),
                "Exception error message is not correct");
    }

    @DisplayName("Change status on not existing place")
    @Test
    public void testChangeStatus_whenPlaceDoesNotExist_throwsResourceNotFoundException() {
        //Arrange
        String expectedExceptionMessage = "Place not found";
        Status status = Status.DISABLE;
        when(placeRepository.findById(place.getId()))
                .thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                placeService.changeStatus(place.getId(), status));

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(),
                "Exception error message is not correct");
    }

    @DisplayName("Change place status to the same status")
    @Test
    public void testChangeStatus_whenChangingStatusEqualActualPlaceStatus_throwsIllegalStateException() {
        //Arrange
        String expectedExceptionMessage = "Place already has this status";
        Status status = Status.FREE;

        when(placeRepository.findById(place.getId()))
                .thenReturn(Optional.of(place));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
                placeService.changeStatus(place.getId(), status));

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(),
                "Exception error message is not correct");
    }

    @DisplayName("Change place status on place with occupied status")
    @Test
    public void testChangeStatus_whenPlaceStatusIsOccupied_thenThrowsIllegalStateException() {
        //Arrange
        place.setStatus(Status.OCCUPIED);
        String expectedExceptionMessage = "Can not change status, because place is occupied";
        Status status = Status.FREE;

        when(placeRepository.findById(place.getId()))
                .thenReturn(Optional.of(place));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
                placeService.changeStatus(place.getId(), status));

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(),
                "Exception error message is not correct");
    }

    @DisplayName("Delete place with valid details")
    @Test
    public void testDeletePlace_whenPlaceDetailsAreValid_returnsNothing() {
        //Arrange
        when(placeRepository.findById(place.getId()))
                .thenReturn(Optional.of(place));

        //Act
        placeService.delete(place.getId());

        //Assert
        verify(placeRepository).deleteById(place.getId());
    }

    @DisplayName("Delete non-existing place")
    @Test
    public void testDeletePlace_whenPlaceDoesNotExist_throwsResourceNotFoundException() {
        //Arrange
        String expectedExceptionMessage = "Place not found";
        when(placeRepository.findById(place.getId()))
                .thenReturn(Optional.empty());

        //Act && Assert
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                placeService.delete(place.getId()));

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(),
                "Exception error message is not correct");
    }

    @DisplayName("Delete place with occupied status throws IllegalStateException")
    @Test
    public void testDeletePlace_whenPlaceStatusIsOccupied_throwsIllegalStateException() {
        //Arrange
        place.setStatus(Status.OCCUPIED);
        String expectedExceptionMessage = "Can not delete occupied place";
        when(placeRepository.findById(place.getId()))
                .thenReturn(Optional.of(place));

        //Act && Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
                placeService.delete(place.getId()));

        //Assert
        assertEquals(expectedExceptionMessage, thrown.getMessage(),
                "Exception error message is not correct");
    }

}
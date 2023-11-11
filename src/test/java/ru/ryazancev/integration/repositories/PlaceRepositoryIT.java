package ru.ryazancev.integration.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ryazancev.integration.BaseIT;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlaceRepositoryIT extends BaseIT {

    @Autowired
    private PlaceRepository placeRepository;

    @DisplayName("Test find places by zone id")
    @Test
    public void testFindAllPlacesByZoneId_returnsListOfPlaces() {
        //Arrange
        Long zoneId = 1L;

        //Act
        List<Place> places = placeRepository.findAllByZoneId(zoneId);

        //Assert
        assertNotNull(places);
        assertEquals(3, places.size());
    }

    @DisplayName("Find all occupied places by user id")
    @Test
    public void testFindAllOccupiedPlacesByUserId_returnsListOfPlaces() {
        //Arrange
        Long userId = 1L;

        //Act
        List<Place> occupiedPlaces = placeRepository.findAllOccupiedByUserId(userId);

        //Assert
        assertNotNull(occupiedPlaces);
        assertEquals(2, occupiedPlaces.size());
    }

    @DisplayName("Assign place to zone")
    @Test
    public void testAssignPlaceToZone_returnsNothing() {
        //Arrange
        Long zoneId = 1L;
        Place place = Place.builder()
                .number(100)
                .status(Status.FREE)
                .build();

        Place savedPlace = placeRepository.save(place);

        //Act
        placeRepository.assignToZone(place.getId(), zoneId);

        //Assert
        List<Place> zonePlaces = placeRepository.findAllByZoneId(zoneId);
        boolean placeIsAssigned = zonePlaces.stream()
                .anyMatch(p -> p.getNumber().equals(savedPlace.getNumber()));

        assertTrue(placeIsAssigned);
    }
}
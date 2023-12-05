package ru.ryazancev.integration.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ryazancev.integration.BaseIT;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.PlaceStatus;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlaceRepositoryIT extends BaseIT {

    @Autowired
    private PlaceRepository placeRepository;

    private final Long ZONE_ID = 1L;

    @DisplayName("Test find places by zone id")
    @Test
    public void testFindAllPlacesByZoneId_returnsListOfPlaces() {
        //Act
        List<Place> places = placeRepository.findAllByZoneId(ZONE_ID);

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
        List<Place> occupiedPlaces =
                placeRepository.findAllOccupiedByUserId(userId);

        //Assert
        assertNotNull(occupiedPlaces);
        assertEquals(2, occupiedPlaces.size());
    }


    @DisplayName("Count all places by zone id")
    @Test
    public void testCountAllPlacesByZoneId_returnsIntCount() {
        //Arrange
        int expected = 3;

        //Act
        int actual = placeRepository.countAllPlacesByZoneId(ZONE_ID);

        //Assert
        assertEquals(expected, actual);
    }

    @DisplayName("Count free places by zone id")
    @Test
    public void testCountFreePlacesByZoneId_returnsIntCount() {
        //Arrange
        int expected = 2;

        //Act
        int actual = placeRepository.countFreePlacesByZoneId(ZONE_ID);

        //Assert
        assertEquals(expected, actual);
    }


    @DisplayName("Assign place to zone")
    @Test
    public void testAssignPlaceToZone_returnsNothing() {
        //Arrange
        Place place = Place.builder()
                .number(100)
                .status(PlaceStatus.FREE)
                .build();

        Place savedPlace = placeRepository.save(place);

        //Act
        placeRepository.assignToZone(place.getId(), ZONE_ID);

        //Assert
        List<Place> zonePlaces = placeRepository.findAllByZoneId(ZONE_ID);
        boolean placeIsAssigned =
                zonePlaces
                        .stream()
                        .anyMatch(p ->
                                p.getNumber().equals(savedPlace.getNumber()));

        assertTrue(placeIsAssigned);
    }
}
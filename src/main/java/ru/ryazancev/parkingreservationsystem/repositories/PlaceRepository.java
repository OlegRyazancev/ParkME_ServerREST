package ru.ryazancev.parkingreservationsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query(value = """
            SELECT *
            FROM places p
                      JOIN zones_places zp on p.id = zp.place_id
            WHERE zp.zone_id = :zoneId
            """, nativeQuery = true)
    List<Place> findAllByZoneId(@Param("zoneId") Long zoneId);

    @Query(value = """
            SELECT p.*
            FROM places p
                     JOIN reservations r ON p.id = r.place_id
            WHERE r.user_id = :userId
            """, nativeQuery = true)
    List<Place> findAllOccupiedByUserId(@Param("userId") Long userId);


    @Modifying
    @Query(value = """
            INSERT INTO zones_places(zone_id, place_id)
            VALUES (:zoneId, :placeId)
            """, nativeQuery = true)
    void assignToZone(@Param("placeId") Long placeId, @Param("zoneId") Long zoneId);
}

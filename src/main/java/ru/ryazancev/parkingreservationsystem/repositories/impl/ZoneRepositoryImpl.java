package ru.ryazancev.parkingreservationsystem.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.repositories.ZoneRepository;
import ru.ryazancev.parkingreservationsystem.repositories.rowmappers.PlaceRowMapper;
import ru.ryazancev.parkingreservationsystem.repositories.rowmappers.ZoneRowMapper;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceMappingException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ZoneRepositoryImpl implements ZoneRepository {

    private final DataSource dataSource;

    String FIND_ALL = """
            SELECT z.id                                                      as zone_id,
                   z.number                                                  as zone_number,
                   COALESCE(COUNT(p.id) FILTER (WHERE p.status = 'FREE'), 0) as zone_free_places
            FROM zones z
                     LEFT JOIN zones_places zp ON z.id = zp.zone_id
                     LEFT JOIN places p ON zp.place_id = p.id
            GROUP BY z.id, z.number
            ORDER BY z.id
            """;

    private final String FIND_BY_ID = """
            SELECT z.id                                                      as zone_id,
                   z.number                                                  as zone_number,
                   COALESCE(COUNT(p.id) FILTER (WHERE p.status = 'FREE'), 0) as zone_free_places
            FROM zones z
                     LEFT JOIN zones_places zp ON z.id = zp.zone_id
                     LEFT JOIN places p ON zp.place_id = p.id
            WHERE z.id = ?
            GROUP BY z.id, z.number
            """;

    private final String FIND_BY_NUMBER = """
            SELECT z.id                                                      as zone_id,
                   z.number                                                  as zone_number,
                   COALESCE(COUNT(p.id) FILTER (WHERE p.status = 'FREE'), 0) as zone_free_places
            FROM zones z
                     LEFT JOIN zones_places zp ON z.id = zp.zone_id
                     LEFT JOIN places p ON zp.place_id = p.id
            WHERE z.number = ?
            GROUP BY z.id, z.number
            """;


    private final String FIND_PLACE_BY_ZONE_NUMBER_AND_PLACE_NUMBER = """
            SELECT p.id     AS place_id,
                   p.number AS place_number,
                   p.status AS place_status
            FROM places p
                     JOIN zones_places zp ON p.id = zp.place_id
                     JOIN zones z ON z.id = zp.zone_id
            WHERE z.number = ?
              AND p.number = ?;""";


    private final String FIND_OCCUPIED_PLACES_BY_ZONE_ID = """
            SELECT p.id     as place_id,
                   p.number as place_number,
                   p.status as place_status
            FROM places p
                     JOIN zones_places zp on p.id = zp.place_id
            WHERE zone_id = ?
              AND p.status LIKE 'OCCUPIED';
            """;
    private final String CREATE = """
            INSERT INTO zones(number)
            VALUES (?)
            """;

    private final String UPDATE = """
            UPDATE zones
            SET number = ?
            WHERE id = ?
            """;

    private final String DELETE_ZONE = """
            DELETE
            FROM zones
            WHERE id = ?;
            """;


    @Override
    public List<Zone> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL)) {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return ZoneRowMapper.mapRows(resultSet);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding all zones");
        }
    }

    @Override
    public Optional<Zone> findById(Long zoneId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {

            preparedStatement.setLong(1, zoneId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.ofNullable(ZoneRowMapper.mapRow(resultSet));
            }

        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding zone by id");
        }
    }

    @Override
    public Optional<Zone> findByNumber(Integer number) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NUMBER)) {

            preparedStatement.setInt(1, number);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.ofNullable(ZoneRowMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Exception while finding zone by number");
        }
    }

    @Override
    public Optional<Place> findPlaceByZoneNumberAndPlaceNumber(Integer zoneNumber, Integer placeNumber) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_PLACE_BY_ZONE_NUMBER_AND_PLACE_NUMBER)) {

            preparedStatement.setInt(1, zoneNumber);
            preparedStatement.setInt(2, placeNumber);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.ofNullable(PlaceRowMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Exception while finding zone by number");
        }
    }

    @Override
    public List<Place> findOccupiedPlacesByZoneId(Long zoneId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_OCCUPIED_PLACES_BY_ZONE_ID)) {

            preparedStatement.setLong(1, zoneId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return PlaceRowMapper.mapRows(resultSet);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding occupied places by zone id");
        }
    }

    @Override
    public void create(Zone zone) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, zone.getNumber());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                resultSet.next();
                zone.setId(resultSet.getLong(1));
                zone.setFreePlaces(0);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while creating zone");
        }
    }

    @Override
    public void update(Zone zone) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {

            preparedStatement.setInt(1, zone.getNumber());
            preparedStatement.setLong(2, zone.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while updating zone");
        }
    }

    @Override
    public void delete(Long zoneId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_ZONE)) {
            statement.setLong(1, zoneId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while deleting zone");
        }
    }
}

package ru.ryazancev.parkingreservationsystem.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;
import ru.ryazancev.parkingreservationsystem.repositories.DataSourceConfig;
import ru.ryazancev.parkingreservationsystem.repositories.ZoneRepository;
import ru.ryazancev.parkingreservationsystem.repositories.mappers.PlaceRowMapper;
import ru.ryazancev.parkingreservationsystem.repositories.mappers.ZoneRowMapper;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceMappingException;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ZoneRepositoryImpl implements ZoneRepository {

    private final DataSourceConfig dataSourceConfig;

    private final String FIND_ALL = """
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
    private final String FIND_NON_FREE_PLACES_BY_ZONE_ID = """
            SELECT p.id     as place_id,
                   p.number as place_number,
                   p.status as place_status
            FROM places p
                     JOIN zones_places zp on p.id = zp.place_id
            WHERE zone_id = ?
              AND p.status != 'FREE';
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
    private final String DELETE_ASSOCIATED_PLACES = """
            DELETE
            FROM places
            WHERE id IN (SELECT place_id FROM zones_places WHERE zone_id = ?);
            """;

    @Override
    public List<Zone> findAll() {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return ZoneRowMapper.mapRows(resultSet);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding all zones");
        }
    }


    @Override
    public Optional<Zone> findById(Long zoneId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID);

            preparedStatement.setLong(1, zoneId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.ofNullable(ZoneRowMapper.mapRow(resultSet));
            }

        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding zone by id");
        }
    }

    @Override
    public List<Place> findNonFreePlacesByZoneId(Long zoneId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_NON_FREE_PLACES_BY_ZONE_ID);
            preparedStatement.setLong(1, zoneId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return PlaceRowMapper.mapRows(resultSet);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding non-free places by zone id");
        }
    }

    @Override
    public Optional<Zone> findByNumber(Integer number) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NUMBER);

            preparedStatement.setInt(1, number);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.ofNullable(ZoneRowMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Exception while finding zone by number");
        }
    }

    @Override
    public void create(Zone zone) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS);

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
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE);

            preparedStatement.setInt(1, zone.getNumber());
            preparedStatement.setLong(2, zone.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while updating zone");
        }
    }

    @Override
    public void delete(Long zoneId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement firstPreparedStatement = connection.prepareStatement(DELETE_ASSOCIATED_PLACES)) {
                firstPreparedStatement.setLong(1, zoneId);
                firstPreparedStatement.executeUpdate();
            }

            try (PreparedStatement secondPreparedStatement = connection.prepareStatement(DELETE_ZONE);) {
                secondPreparedStatement.setLong(1, zoneId);
                secondPreparedStatement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while deleting zone");
        }
    }
}

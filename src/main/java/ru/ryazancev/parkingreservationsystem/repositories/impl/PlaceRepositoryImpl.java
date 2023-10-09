package ru.ryazancev.parkingreservationsystem.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.sql.SQL;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.repositories.DataSourceConfig;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.repositories.mappers.PlaceRowMapper;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceMappingException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepository {

    private final DataSourceConfig dataSourceConfig;

    private final String FIND_BY_ID = """
            SELECT p.id     as place_id,
                   p.number as place_number,
                   p.status as place_status
            FROM places p
            WHERE p.id = ?;
            """;

    private final String FIND_ALL_BY_ZONE_ID = """
            SELECT p.id     as place_id,
                   p.number as place_number,
                   p.status as place_status
            FROM places p
                     LEFT JOIN zones_places zp on p.id = zp.place_id
            WHERE zp.zone_id = ?;
            """;

    private final String ASSIGN = """
            INSERT INTO zones_places (zone_id, place_id)
            VALUES (?, ?)
            """;

    private final String CREATE = """
            INSERT INTO places(number)
            VALUES (?)
            """;

    private final String MAKE_DISABLE = """
            UPDATE places
            SET status = 'DISABLE'
            WHERE id = ?;
            """;
    private final String DELETE = """
            DELETE
            FROM places
            WHERE id = ?
            """;


    @Override
    public Optional<Place> findById(Long placeId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID);

            preparedStatement.setLong(1, placeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.ofNullable(PlaceRowMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding place by id");
        }
    }

    @Override
    public List<Place> findAllByZoneId(Long zoneId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_ZONE_ID);

            preparedStatement.setLong(1, zoneId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return PlaceRowMapper.mapRows(resultSet);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding places by zone id");
        }
    }

    @Override
    public void assignPlaceToZone(Long placeId, Long zoneId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(ASSIGN);

            preparedStatement.setLong(1, zoneId);
            preparedStatement.setLong(2, placeId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while assigning place to zone");
        }
    }

    @Override
    public void create(Place place) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, place.getNumber());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                resultSet.next();
                place.setId(resultSet.getLong(1));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while creating place");
        }
    }

    @Override
    public void makeDisable(Place place) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(MAKE_DISABLE);

            preparedStatement.setLong(1, place.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while making place disable");
        }
    }

    @Override
    public void delete(Long placeId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE);

            preparedStatement.setLong(1, placeId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while deleting place");
        }
    }
}

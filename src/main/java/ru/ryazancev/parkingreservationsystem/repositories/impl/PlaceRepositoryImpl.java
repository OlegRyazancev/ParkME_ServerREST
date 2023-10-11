package ru.ryazancev.parkingreservationsystem.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;
import ru.ryazancev.parkingreservationsystem.repositories.PlaceRepository;
import ru.ryazancev.parkingreservationsystem.repositories.rowmappers.PlaceRowMapper;
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
public class PlaceRepositoryImpl implements PlaceRepository {

    private final DataSource dataSource;

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

    private final String FIND_ALL_OCCUPIED_BY_USER_ID = """
            SELECT p.id     as place_id,
                   p.number as place_number,
                   p.status as place_status
            FROM places p
                     JOIN cars_places cp ON p.id = cp.place_id
                     JOIN users_cars uc ON cp.car_id = uc.car_id
            WHERE uc.user_id = ?
              AND p.status = 'OCCUPIED'
            """;

    private final String ASSIGN = """
            INSERT INTO zones_places (zone_id, place_id)
            VALUES (?, ?)
            """;

    private final String CREATE = """
            INSERT INTO places(number, status)
            VALUES (?, 'FREE')
            """;

    private final String CHANGE_STATUS = """
            UPDATE places
            SET status = ?
            WHERE id = ?;
            """;

    private final String DELETE = """
            DELETE
            FROM places
            WHERE id = ?
            """;

    @Override
    public Optional<Place> findById(Long placeId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {

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
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_ZONE_ID)) {
            preparedStatement.setLong(1, zoneId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return PlaceRowMapper.mapRows(resultSet);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding places by zone id");
        }
    }

    @Override
    public List<Place> findAllOccupiedByUserId(Long userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_OCCUPIED_BY_USER_ID)) {
            preparedStatement.setLong(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return PlaceRowMapper.mapRows(resultSet);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding occupied places by user id");
        }
    }

    @Override
    public void assignToZoneById(Long placeId, Long zoneId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ASSIGN)) {

            preparedStatement.setLong(1, zoneId);
            preparedStatement.setLong(2, placeId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while assigning place to zone");
        }
    }

    @Override
    public void create(Place place) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS)) {

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
    public void changeStatus(Place place, Status status) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CHANGE_STATUS)) {

            preparedStatement.setString(1, status.name());
            preparedStatement.setLong(2, place.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while changing place status");
        }
    }

    @Override
    public void delete(Long placeId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {

            preparedStatement.setLong(1, placeId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while deleting place");
        }
    }


}

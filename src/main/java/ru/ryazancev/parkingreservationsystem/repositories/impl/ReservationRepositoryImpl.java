package ru.ryazancev.parkingreservationsystem.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;
import ru.ryazancev.parkingreservationsystem.repositories.rowmappers.ReservationRowMapper;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceMappingException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final DataSource dataSource;

    private final String FIND_ALL = """
            SELECT r.id as reservation_id,
                   r.time_to   as time_to,
                   r.time_from as time_from
            FROM reservations r
            """;
    private final String FIND_BY_ID = """
            SELECT r.id        as reservation_id,
                   r.time_from as time_from,
                   r.time_to   as time_to,
                   z.id        as zone_id,
                   z.number    as zone_number,
                   p.id        as place_id,
                   p.number    as place_number,
                   p.status    as place_status,
                   c.id        as car_id,
                   c.number    as car_number
            FROM reservations r
                     LEFT JOIN reservations_places rp ON r.id = rp.reservation_id
                     LEFT JOIN places p ON rp.place_id = p.id
                     LEFT JOIN zones_places zp ON p.id = zp.place_id
                     LEFT JOIN zones z ON zp.zone_id = z.id
                     LEFT JOIN cars_places cp on p.id = cp.place_id
                     LEFT JOIN cars c ON cp.car_id = c.id
            WHERE r.id = ?;
            """;

    private final String FIND_ALL_BY_USER_ID = """
            SELECT r.id        as reservation_id,
                   r.time_from as time_from,
                   r.time_to   as time_to
            FROM reservations r
                     LEFT JOIN reservations_places rp ON r.id = rp.reservation_id
                     LEFT JOIN places p ON rp.place_id = p.id
                     LEFT JOIN zones_places zp ON p.id = zp.place_id
                     LEFT JOIN zones z ON zp.zone_id = z.id
                     LEFT JOIN cars_places cp on p.id = cp.place_id
                     LEFT JOIN cars c ON cp.car_id = c.id
                     LEFT JOIN users_cars uc on c.id = uc.car_id
                     LEFT JOIN users u ON uc.user_id = u.id
            WHERE u.id = ?
            """;

    private final String ASSIGN_RESERVATION_TO_PLACE = """
            INSERT INTO reservations_places (reservation_id, place_id)
            VALUES (?, ?);
            """;

    private final String ASSIGN_PLACE_TO_CAR = """
            INSERT INTO cars_places(car_id, place_id)
            VALUES (?, ?);
            """;

    private final String CREATE = """
            INSERT INTO reservations(time_from, time_to)
            VALUES (?, ?)
            """;

    private final String UPDATE_TIME_TO = """
            UPDATE reservations
            SET time_to = ?
            WHERE id = ?;
            """;

    private final String DELETE = """
            DELETE
            FROM reservations r
            WHERE r.id =?;
            """;

    private final String DELETE_RELATIONSHIPS = """
            DELETE FROM cars_places cp
            WHERE EXISTS (
                SELECT 1
                FROM reservations_places rp
                WHERE rp.reservation_id = ?
                AND rp.place_id = cp.place_id
            );
                        
            """;

    @Override
    public List<Reservation> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return ReservationRowMapper.mapRows(resultSet);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding all reservations");
        }
    }

    @Override
    public Optional<Reservation> findById(Long reservationId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {

            preparedStatement.setLong(1, reservationId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.ofNullable(ReservationRowMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding reservation by id");
        }
    }

    @Override
    public List<Reservation> findAllByUserId(Long userId) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_USER_ID);

            preparedStatement.setLong(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return ReservationRowMapper.mapRows(resultSet);
            }

        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding reservations by user id");
        }
    }

    @Override
    public void assignToUser(Reservation reservation) {
        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement first = connection.prepareStatement(ASSIGN_PLACE_TO_CAR)) {
                first.setLong(1, reservation.getCar().getId());
                first.setLong(2, reservation.getPlace().getId());
                first.executeUpdate();
            }

            try (PreparedStatement second = connection.prepareStatement(ASSIGN_RESERVATION_TO_PLACE)) {
                second.setLong(1, reservation.getId());
                second.setLong(2, reservation.getPlace().getId());
                second.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            throw new IllegalStateException("Error while assign reservation to user by car");
        }
    }

    @Override
    public void create(Reservation reservation) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS);

            preparedStatement.setTimestamp(1, Timestamp.valueOf(reservation.getTimeFrom()));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(reservation.getTimeTo()));
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                resultSet.next();
                reservation.setId(resultSet.getLong(1));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while creating reservation");
        }
    }

    @Override
    public void update(Reservation reservation) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TIME_TO);

            preparedStatement.setTimestamp(1, Timestamp.valueOf(reservation.getTimeTo()));
            preparedStatement.setLong(2, reservation.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while changing reservation's time to");
        }
    }

    @Override
    public void delete(Long reservationId) {

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_RELATIONSHIPS)) {
                preparedStatement.setLong(1, reservationId);
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {
                preparedStatement.setLong(1, reservationId);
                preparedStatement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while deleting reservation");
        }
    }
}

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
            SELECT r.id       as reservation_id,
                   r.time_to  as time_to,
                   r.time_to as time_from,
                   r.user_id  as user_id,
                   u.name     as user_name,
                   u.email    as user_email,
                   u.password as user_password,
                   r.zone_id  as zone_id,
                   z.number   as zone_number,
                   r.place_id as place_id,
                   p.number   as place_number,
                   p.status as place_status,
                   r.car_id   as car_id,
                   c.number   as car_number
            FROM reservations r
                     LEFT JOIN users u ON r.user_id = u.id
                     LEFT JOIN zones z ON r.zone_id = z.id
                     LEFT JOIN cars c ON r.car_id = c.id
                     LEFT JOIN places p ON r.place_id = p.id
            WHERE r.id = ?;
            """;

    private final String FIND_ALL_BY_USER_ID = """
            SELECT r.id        as reservation_id,
                   r.time_from as time_from,
                   r.time_to   as time_to
            FROM reservations r
            WHERE r.user_id = ?
            """;

    private final String CREATE = """
            INSERT INTO reservations(time_from, time_to, user_id, car_id, zone_id, place_id)
            VALUES (?, ?, ?, ?, ?, ?)
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
    public void create(Reservation reservation) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS);

            preparedStatement.setTimestamp(1, Timestamp.valueOf(reservation.getTimeFrom()));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(reservation.getTimeTo()));
            preparedStatement.setLong(3, reservation.getUser().getId());
            preparedStatement.setLong(4, reservation.getCar().getId());
            preparedStatement.setLong(5, reservation.getZone().getId());
            preparedStatement.setLong(6, reservation.getPlace().getId());
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

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {

            preparedStatement.setLong(1, reservationId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new ResourceMappingException("Error while deleting reservation");
        }
    }
}

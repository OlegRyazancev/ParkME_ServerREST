package ru.ryazancev.parkingreservationsystem.repositories.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.repositories.DataSourceConfig;
import ru.ryazancev.parkingreservationsystem.repositories.ReservationRepository;
import ru.ryazancev.parkingreservationsystem.repositories.mappers.ReservationRowMapper;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceMappingException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final DataSourceConfig dataSourceConfig;

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

    @Override
    public List<Reservation> findAll() {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return ReservationRowMapper.mapRows(resultSet);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding all reservations");
        }
    }

    @Override
    public Optional<Reservation> findById(Long reservationId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID);

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
        return null;
    }

    @Override
    public void assignToUserById(Reservation reservation, Long userId) {

    }

    @Override
    public void extend(Reservation reservation) {
    }

    @Override
    public void create(Reservation reservation) {

    }

    @Override
    public void delete(Long reservationId) {

    }
}

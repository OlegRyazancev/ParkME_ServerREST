package ru.ryazancev.parkingreservationsystem.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.reservation.Reservation;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.repositories.rowmappers.CarRowMapper;
import ru.ryazancev.parkingreservationsystem.repositories.rowmappers.ReservationRowMapper;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceMappingException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CarRepositoryImpl implements CarRepository {

    private final DataSource dataSource;

    private final String FIND_ALL = """
            SELECT c.id     as car_id,
                   c.number as car_number
            FROM cars c;
            """;

    private final String FIND_ALL_BY_USER_ID = """
            SELECT c.id     as car_id,
                   c.number as car_number
            FROM cars c
                     LEFT JOIN users_cars uc on c.id = uc.car_id
            WHERE uc.user_id = ?;
            """;

    private final String FIND_BY_ID = """
            SELECT c.id     as car_id,
                   c.number as car_number
            FROM cars c
            WHERE c.id = ?;
                        
            """;

    private final String FIND_BY_NUMBER = """
            SELECT c.id     as car_id,
                   c.number as car_number
            FROM cars c
            WHERE c.number = ?
            """;

    private final String ASSIGN = """
            INSERT INTO users_cars (user_id, car_id)
            VALUES (?, ?)
            """;

    private final String CREATE = """
            INSERT INTO cars (number)
            VALUES (?)
            """;

    private final String UPDATE = """
            UPDATE cars
            SET number = ?
            WHERE id = ?
            """;

    private final String DELETE = """
            DELETE
            FROM cars
            WHERE id = ?;
            """;

    private final String FIND_RESERVATIONS_BY_CAR_ID = """
            SELECT r.id        as reservation_id,
                   r.time_from as time_from,
                   r.time_to   as time_to
            FROM cars c
                     LEFT JOIN cars_places cp on c.id = cp.car_id
                     LEFT JOIN places p ON cp.place_id = p.id
                     LEFT JOIN reservations_places rp ON p.id = rp.place_id
                     LEFT JOIN reservations r ON rp.reservation_id = r.id
            WHERE c.id = ?;
            """;
    private final String EXISTS_RESERVATION_BY_CAR_NUMBER = """
            SELECT EXISTS (SELECT 1
                           FROM reservations r
                                    JOIN reservations_places rp ON r.id = rp.reservation_id
                                    JOIN places p ON rp.place_id = p.id
                                    JOIN cars_places cp ON p.id = cp.place_id
                                    JOIN cars c ON cp.car_id = c.id
                           WHERE c.number = ?) AS exists_reservation;            
            """;

    @Override
    public List<Car> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return CarRowMapper.mapRows(resultSet);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding all cars");
        }
    }

    @Override
    public List<Car> findAllByUserId(Long userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_USER_ID)) {

            preparedStatement.setLong(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return CarRowMapper.mapRows(resultSet);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding cars by user id");
        }
    }

    @Override
    public Optional<Car> findById(Long carId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {

            preparedStatement.setLong(1, carId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.ofNullable(CarRowMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding car by id");
        }
    }

    @Override
    public Optional<Car> findByNumber(String number) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NUMBER);) {
            preparedStatement.setString(1, number);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.ofNullable(CarRowMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding car by number");
        }
    }

    @Override
    public Optional<Reservation> findReservationByCarId(Long carId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_RESERVATIONS_BY_CAR_ID)) {

            preparedStatement.setLong(1, carId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.ofNullable(ReservationRowMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding reservation by car id");
        }
    }

    @Override
    public boolean existsReservationByCarNumber(String number) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(EXISTS_RESERVATION_BY_CAR_NUMBER)) {


            statement.setString(1, number);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while checkin if car has reservations");
        }
    }

    @Override
    public void assignToUserById(Long carId, Long userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(ASSIGN)) {


            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, carId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while assigning car to user");
        }
    }

    @Override
    public void create(Car car) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, car.getNumber());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                resultSet.next();
                car.setId(resultSet.getLong(1));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while creating car");
        }
    }

    @Override
    public void update(Car car) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {

            preparedStatement.setString(1, car.getNumber());
            preparedStatement.setLong(2, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while updating car");
        }
    }

    @Override
    public void delete(Long carId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {

            preparedStatement.setLong(1, carId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while deleting car");
        }
    }
}

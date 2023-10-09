package ru.ryazancev.parkingreservationsystem.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.repositories.CarRepository;
import ru.ryazancev.parkingreservationsystem.repositories.DataSourceConfig;
import ru.ryazancev.parkingreservationsystem.repositories.mappers.CarRowMapper;
import ru.ryazancev.parkingreservationsystem.util.exceptions.ResourceMappingException;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CarRepositoryImpl implements CarRepository {

    private final DataSourceConfig dataSourceConfig;

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


    @Override
    public List<Car> findAll() {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return CarRowMapper.mapRows(resultSet);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding all cars");
        }
    }

    @Override
    public List<Car> findAllByUserId(Long userId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_USER_ID);

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
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID);

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
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NUMBER);

            preparedStatement.setString(1, number);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return Optional.ofNullable(CarRowMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding car by number");
        }
    }

    @Override
    public void assignCarToUserById(Long carId, Long userId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(ASSIGN);

            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, carId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while assigning car to user");
        }
    }

    @Override
    public void create(Car car) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS);

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
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE);

            preparedStatement.setString(1, car.getNumber());
            preparedStatement.setLong(2, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while updating car");
        }
    }

    @Override
    public void delete(Long carId) {
        try {
            Connection connection = dataSourceConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE);

            preparedStatement.setLong(1, carId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while deleting car");
        }
    }
}

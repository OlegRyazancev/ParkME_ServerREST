package ru.ryazancev.parkingreservationsystem.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.user.Role;
import ru.ryazancev.parkingreservationsystem.models.user.User;
import ru.ryazancev.parkingreservationsystem.repositories.UserRepository;
import ru.ryazancev.parkingreservationsystem.repositories.rowmappers.UserRowMapper;
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
public class UserRepositoryImpl implements UserRepository {

    private final DataSource dataSource;

    private final String FIND_ALL = """
            SELECT u.id       AS user_id,
                   u.name     AS user_name,
                   u.email    AS user_email,
                   u.password AS user_password
            FROM users u
            """;

    private final String FIND_BY_ID = """
            SELECT u.id       AS user_id,
                   u.name     AS user_name,
                   u.email    AS user_email,
                   u.password AS user_password,
                   ur.role    AS user_role,
                   c.id       as car_id,
                   c.number   as car_number
            FROM users u
                     LEFT JOIN users_roles ur ON u.id = ur.user_id
                     LEFT JOIN users_cars uc ON u.id = uc.user_id
                     LEFT JOIN cars c ON uc.car_id = c.id
            WHERE u.id = ?
                        """;

    private final String FIND_BY_EMAIL = """
            SELECT u.id       AS user_id,
                   u.name     AS user_name,
                   u.email    AS user_email,
                   u.password AS user_password,
                   ur.role    AS user_role,
                   c.id       as car_id,
                   c.number   as car_number
            FROM users u
                     LEFT JOIN users_roles ur ON u.id = ur.user_id
                     LEFT JOIN users_cars uc ON u.id = uc.user_id
                     LEFT JOIN cars c ON uc.car_id = c.id
            WHERE u.email = ?
                        """;

    private final String INSERT_USER_ROLE = """
            INSERT INTO users_roles (user_id, role)
            VALUES(?, ?)
            """;

    private final String CREATE = """
            INSERT INTO users (name, email, password)
            VALUES (?, ?, ?)
            """;

    private final String UPDATE = """
            UPDATE users
            SET name     = ?,
                email    = ?,
                password = ?
            WHERE id = ?
            """;

    private final String DELETE = """
            DELETE FROM users
            WHERE id = ?
            """;

    private final String IS_CAR_OWNER = """
            SELECT EXISTS(SELECT 1
                          FROM users_cars
                          WHERE user_id = ?
                            AND car_id = ?)
            """;

    private final String IS_RESERVATION_OWNER = """
            SELECT EXISTS(SELECT 1
                          FROM users_cars uc
                                   LEFT JOIN cars_places cp ON uc.car_id = cp.car_id
                                   LEFT JOIN reservations_places rp on cp.place_id = rp.place_id
                          WHERE uc.user_id = ?
                            AND rp.reservation_id = ?)
            """;

    @Override
    public List<User> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                return UserRowMapper.mapRows(resultSet);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding all users");
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID,
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_READ_ONLY)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return Optional.ofNullable(UserRowMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding user by id");
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_EMAIL,
                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_READ_ONLY)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return Optional.ofNullable(UserRowMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding user by email");
        }
    }

    @Override
    public Boolean isCarOwner(Long userId, Long carId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(IS_CAR_OWNER)) {
            statement.setLong(1, userId);
            statement.setLong(2, carId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding if user is car owner");
        }
    }

    @Override
    public Boolean isReservationOwner(Long userId, Long reservationId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(IS_RESERVATION_OWNER)) {
            statement.setLong(1, userId);
            statement.setLong(2, reservationId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while finding if user is car owner");
        }
    }

    @Override
    public void insertUserRole(Long userId, Role role) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_USER_ROLE)) {

            statement.setLong(1, userId);
            statement.setString(2, role.name());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while inserting user role");
        }
    }

    @Override
    public void create(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());

            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                resultSet.next();
                user.setId(resultSet.getLong(1));
            }
        } catch (SQLException e) {
            throw new ResourceMappingException("Error whilr creating user");
        }
    }

    @Override
    public void update(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setLong(4, user.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while updating user");
        }
    }

    @Override
    public void delete(Long userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {

            statement.setLong(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ResourceMappingException("Error while deleting user");
        }
    }
}

package ru.ryazancev.parkingreservationsystem.repositories.rowmappers;

import lombok.SneakyThrows;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.user.Role;
import ru.ryazancev.parkingreservationsystem.models.user.User;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserRowMapper {
    @SneakyThrows
    public static User mapRow(ResultSet resultSet) {
        Set<Role> roles = new HashSet<>();
        while (resultSet.next()) {
            roles.add(Role.valueOf(resultSet.getString("user_role")));
        }
        resultSet.beforeFirst();
        List<Car> cars = CarRowMapper.mapRows(resultSet);
        resultSet.beforeFirst();

        if (resultSet.next()) {
            User user = new User();

            user.setId(resultSet.getLong("user_id"));
            user.setName(resultSet.getString("user_name"));
            user.setEmail(resultSet.getString("user_email"));
            user.setPassword(resultSet.getString("user_password"));
            user.setRoles(roles);
            user.setCars(cars);

            return user;
        }
        return null;
    }

    @SneakyThrows
    public static List<User> mapRows(ResultSet resultSet) {

        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            User user = new User();

            if (!resultSet.wasNull()) {
                user.setId(resultSet.getLong("user_id"));
                user.setName(resultSet.getString("user_name"));
                user.setEmail(resultSet.getString("user_email"));
                user.setPassword(resultSet.getString("user_password"));
            }
            users.add(user);
        }
        return users;
    }
}

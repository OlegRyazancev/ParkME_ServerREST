package ru.ryazancev.parkingreservationsystem.repositories.rowmappers;

import lombok.SneakyThrows;
import ru.ryazancev.parkingreservationsystem.models.user.User;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserRowMapper {
    @SneakyThrows
    public static User mapRow(ResultSet resultSet) {
        if (resultSet.next()) {
            User user = new User();

            user.setId(resultSet.getLong("user_id"));
            user.setName(resultSet.getString("user_name"));
            user.setEmail(resultSet.getString("user_email"));
            user.setPassword(resultSet.getString("user_password"));

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

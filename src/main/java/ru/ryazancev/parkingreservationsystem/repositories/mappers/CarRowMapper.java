package ru.ryazancev.parkingreservationsystem.repositories.mappers;

import lombok.SneakyThrows;
import ru.ryazancev.parkingreservationsystem.models.car.Car;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CarRowMapper {

    @SneakyThrows
    public static Car mapRow(ResultSet resultSet) {
        if (resultSet.next()) {
            Car car = new Car();

            car.setId(resultSet.getLong("car_id"));
            car.setNumber(resultSet.getString("car_number"));

            return car;
        }
        return null;
    }

    @SneakyThrows
    public static List<Car> mapRows(ResultSet resultSet) {
        List<Car> cars = new ArrayList<>();

        while (resultSet.next()) {
            Car car = new Car();

            if (!resultSet.wasNull()) {
                car.setId(resultSet.getLong("car_id"));
                car.setNumber(resultSet.getString("car_number"));
            }
            cars.add(car);
        }
        return cars;
    }
}

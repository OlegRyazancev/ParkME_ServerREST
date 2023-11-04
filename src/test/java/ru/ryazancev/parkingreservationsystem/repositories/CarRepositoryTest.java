package ru.ryazancev.parkingreservationsystem.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import ru.ryazancev.IntegrationTestBase;
import ru.ryazancev.parkingreservationsystem.models.car.Car;
import ru.ryazancev.parkingreservationsystem.models.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarRepositoryTest extends IntegrationTestBase {

    @Autowired
    private CarRepository carRepository;


    @DisplayName("Find all cars by user id")
    @Test
    public void test() {

        Long userId = 1L;

        List<Car> cars = carRepository.findAllByUserId(userId);

        assertNotNull(cars);
        assertEquals(2, cars.size());
    }

}
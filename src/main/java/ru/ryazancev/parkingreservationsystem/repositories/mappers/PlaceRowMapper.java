package ru.ryazancev.parkingreservationsystem.repositories.mappers;

import lombok.SneakyThrows;
import ru.ryazancev.parkingreservationsystem.models.parking.Place;
import ru.ryazancev.parkingreservationsystem.models.parking.Status;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PlaceRowMapper {
    @SneakyThrows
    public static Place mapRow(ResultSet resultSet) {
        if (resultSet.next()) {
            Place place = new Place();

            place.setId(resultSet.getLong("place_id"));
            place.setNumber(resultSet.getInt("place_number"));
            place.setStatus(Status.valueOf(resultSet.getString("place_status")));

            return place;
        }
        return null;
    }

    @SneakyThrows
    public static List<Place> mapRows(ResultSet resultSet) {
        List<Place> places = new ArrayList<>();

        while (resultSet.next()) {
            Place place = new Place();

            if (!resultSet.wasNull()) {
                place.setId(resultSet.getLong("place_id"));
                place.setNumber(resultSet.getInt("place_number"));
                place.setStatus(Status.valueOf(resultSet.getString("place_status")));
            }
            places.add(place);
        }
        return places;
    }
}

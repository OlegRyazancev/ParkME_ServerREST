package ru.ryazancev.parkingreservationsystem.repositories.rowmappers;

import lombok.SneakyThrows;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ZoneRowMapper {

    @SneakyThrows
    public static Zone mapRow(ResultSet resultSet) {
        if (resultSet.next()) {
            Zone zone = new Zone();

            zone.setId(resultSet.getLong("zone_id"));
            zone.setNumber(resultSet.getInt("zone_number"));
            zone.setFreePlaces(resultSet.getInt("zone_free_places"));
            return zone;
        }
        return null;
    }

    @SneakyThrows
    public static List<Zone> mapRows(ResultSet resultSet) {
        List<Zone> zones = new ArrayList<>();

        while (resultSet.next()) {
            Zone zone = new Zone();

            if (!resultSet.wasNull()) {
                zone.setId(resultSet.getLong("zone_id"));
                zone.setNumber(resultSet.getInt("zone_number"));
                zone.setFreePlaces(resultSet.getInt("zone_free_places"));
            }

            zones.add(zone);
        }
        return zones;
    }
}


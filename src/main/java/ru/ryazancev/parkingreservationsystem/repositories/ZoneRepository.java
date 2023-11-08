package ru.ryazancev.parkingreservationsystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ryazancev.parkingreservationsystem.models.parking.Zone;

import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

    Optional<Zone> findByNumber(Integer number);

}

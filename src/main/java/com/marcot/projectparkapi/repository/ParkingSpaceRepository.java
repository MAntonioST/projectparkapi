package com.marcot.projectparkapi.repository;

import com.marcot.projectparkapi.entity.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {
    Optional<ParkingSpace> findByCode(String codigo);

    Optional<ParkingSpace> findFirstByStatus(ParkingSpace.ParkingSpaceStatus spaceStatus);
}

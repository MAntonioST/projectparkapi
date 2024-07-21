package com.marcot.projectparkapi.repository;

import com.marcot.projectparkapi.entity.ParkingSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingSpotEntityRepository extends JpaRepository<ParkingSpotEntity, Long> {
    Optional<ParkingSpotEntity> findByCode(String codigo);
}

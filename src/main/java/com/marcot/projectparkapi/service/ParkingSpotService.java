package com.marcot.projectparkapi.service;


import com.marcot.projectparkapi.entity.ParkingSpotEntity;
import com.marcot.projectparkapi.exception.CodeUniqueViolationException;
import com.marcot.projectparkapi.exception.EntityNotFoundException;
import com.marcot.projectparkapi.repository.ParkingSpotEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ParkingSpotService {

    private final ParkingSpotEntityRepository parkingSpotRepository;

    @Transactional
    public ParkingSpotEntity save(ParkingSpotEntity parkingSpot) {
        try {
            return parkingSpotRepository.save(parkingSpot);
        } catch (DataIntegrityViolationException ex) {
            throw new CodeUniqueViolationException(
                    String.format("Parking spot with code '%s' is already registered", parkingSpot.getCode())
            );
        }
    }

    @Transactional(readOnly = true)
    public ParkingSpotEntity findByCode(String code) {
        return parkingSpotRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException(String.format("Parking spot with code '%s' was not found", code))
        );
    }
}

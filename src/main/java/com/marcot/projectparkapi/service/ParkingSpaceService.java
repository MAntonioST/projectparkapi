package com.marcot.projectparkapi.service;


import com.marcot.projectparkapi.entity.ParkingSpace;
import com.marcot.projectparkapi.exception.CodeUniqueViolationException;
import com.marcot.projectparkapi.exception.EntityNotFoundException;
import com.marcot.projectparkapi.repository.ParkingSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.marcot.projectparkapi.entity.ParkingSpace.ParkingSpaceStatus.FREE;

@RequiredArgsConstructor
@Service
public class ParkingSpaceService {

    private final ParkingSpaceRepository parkingSpaceRepository;

    @Transactional
    public ParkingSpace save(ParkingSpace parkingSpot) {
        try {
            return parkingSpaceRepository.save(parkingSpot);
        } catch (DataIntegrityViolationException ex) {
            throw new CodeUniqueViolationException(
                    String.format("Parking spot with code '%s' is already registered", parkingSpot.getCode())
            );
        }
    }

    @Transactional(readOnly = true)
    public ParkingSpace findByCode(String code) {
        return parkingSpaceRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException(String.format("Parking spot with code '%s' was not found", code))
        );
    }


    @Transactional(readOnly = true)
    public ParkingSpace findFirstAvailableSpace() {
        return parkingSpaceRepository.findFirstByStatus(FREE).orElseThrow(
                () -> new EntityNotFoundException("No available parking spot found")
        );
    }
}

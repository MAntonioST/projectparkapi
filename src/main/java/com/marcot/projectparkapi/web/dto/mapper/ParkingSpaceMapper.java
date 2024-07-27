package com.marcot.projectparkapi.web.dto.mapper;


import com.marcot.projectparkapi.entity.ParkingSpace;
import com.marcot.projectparkapi.web.dto.ParkingSpaceCreateDto;
import com.marcot.projectparkapi.web.dto.ParkingSpaceResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpaceMapper {

    public static ParkingSpace toParkingSpot(ParkingSpaceCreateDto dto) {
        return new ModelMapper().map(dto, ParkingSpace.class);
    }

    public static ParkingSpaceResponseDto toDto(ParkingSpace parkingSpot) {
        return new ModelMapper().map(parkingSpot, ParkingSpaceResponseDto.class);
    }
}
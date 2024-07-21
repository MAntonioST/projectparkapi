package com.marcot.projectparkapi.web.dto.mapper;


import com.marcot.projectparkapi.entity.ParkingSpotEntity;
import com.marcot.projectparkapi.web.dto.ParkingSpotCreateDto;
import com.marcot.projectparkapi.web.dto.ParkingSpotResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSpotMapper {

    public static ParkingSpotEntity toParkingSpot(ParkingSpotCreateDto dto) {
        return new ModelMapper().map(dto, ParkingSpotEntity.class);
    }

    public static ParkingSpotResponseDto toDto(ParkingSpotEntity parkingSpot) {
        return new ModelMapper().map(parkingSpot, ParkingSpotResponseDto.class);
    }
}
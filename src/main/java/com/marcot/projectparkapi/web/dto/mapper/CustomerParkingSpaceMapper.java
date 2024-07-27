package com.marcot.projectparkapi.web.dto.mapper;


import com.marcot.projectparkapi.entity.CustomerParkingSpace;
import com.marcot.projectparkapi.web.dto.ParkingCreateDto;
import com.marcot.projectparkapi.web.dto.ParkingResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerParkingSpaceMapper {

    public static CustomerParkingSpace toCustomerParkingSpace(ParkingCreateDto dto) {
        return new ModelMapper().map(dto, CustomerParkingSpace.class);
    }

    public static ParkingResponseDto toDto(CustomerParkingSpace customerParkingSpace) {
        return new ModelMapper().map(customerParkingSpace, ParkingResponseDto.class);
    }
}
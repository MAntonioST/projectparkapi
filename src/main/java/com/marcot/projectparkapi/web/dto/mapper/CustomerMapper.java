package com.marcot.projectparkapi.web.dto.mapper;

import com.marcot.projectparkapi.entity.CustomerEntity;
import com.marcot.projectparkapi.web.dto.CustomerCreateDto;
import com.marcot.projectparkapi.web.dto.CustomerResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerMapper {

    public static CustomerEntity toCustomer(CustomerCreateDto dto){
        return  new ModelMapper().map(dto, CustomerEntity.class);
    }

    public static CustomerResponseDto toDto(CustomerEntity customer){
        return  new ModelMapper().map(customer, CustomerResponseDto.class);
    }

    public static List<CustomerResponseDto> toDtoList(List<CustomerEntity> customerList) {
        return customerList.stream()
                .map(CustomerMapper::toDto)
                .collect(Collectors.toList());
    }
}

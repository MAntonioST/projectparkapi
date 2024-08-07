package com.marcot.projectparkapi.web.dto.mapper;

import com.marcot.projectparkapi.entity.Customer;
import com.marcot.projectparkapi.web.dto.CustomerCreateDto;
import com.marcot.projectparkapi.web.dto.CustomerResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerMapper {

    public static Customer toCustomer(CustomerCreateDto dto){
        return  new ModelMapper().map(dto, Customer.class);
    }

    public static CustomerResponseDto toDto(Customer customer){
        return  new ModelMapper().map(customer, CustomerResponseDto.class);
    }

}

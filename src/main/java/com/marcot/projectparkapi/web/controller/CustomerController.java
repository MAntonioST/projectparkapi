package com.marcot.projectparkapi.web.controller;

import com.marcot.projectparkapi.entity.CustomerEntity;
import com.marcot.projectparkapi.service.CustomerService;
import com.marcot.projectparkapi.web.dto.CustomerCreateDto;
import com.marcot.projectparkapi.web.dto.CustomerResponseDto;
import com.marcot.projectparkapi.web.dto.mapper.CustomerMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody @Valid CustomerCreateDto dto) {
        CustomerEntity entity = customerService.save(dto);
        return ResponseEntity.status(201).body(CustomerMapper.toDto(entity));
    }
}

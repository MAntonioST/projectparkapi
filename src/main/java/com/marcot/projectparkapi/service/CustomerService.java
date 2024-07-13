package com.marcot.projectparkapi.service;


import com.marcot.projectparkapi.entity.CustomerEntity;
import com.marcot.projectparkapi.exception.CpfUniqueViolationException;
import com.marcot.projectparkapi.jwt.JwtUserDetails;
import com.marcot.projectparkapi.repository.CustomerEntityRepository;
import com.marcot.projectparkapi.web.dto.CustomerCreateDto;
import com.marcot.projectparkapi.web.dto.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerEntityRepository customerRepository;
    private final UserService userService;



    public CustomerEntity save(CustomerCreateDto dto) {
        CustomerEntity customer = CustomerMapper.toCustomer(dto);
        // Get the authenticated user details
        JwtUserDetails userDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        customer.setUserEntity(userService.getById(userDetails.getId()));
        try {
            return customerRepository.save(customer);
        } catch ( DataIntegrityViolationException ex) {
            throw new CpfUniqueViolationException(
                    String.format("CPF '%s' cannot be registered, it already exists in the system", customer.getCpf())
            );
        }

    }

    public List<CustomerEntity> findAll() {
        return customerRepository.findAll();
    }

    public CustomerEntity findById(Long id) {
        return customerRepository.findById(id).get();
    }

    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }
}

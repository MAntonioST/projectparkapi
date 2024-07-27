package com.marcot.projectparkapi.service;


import com.marcot.projectparkapi.entity.Customer;
import com.marcot.projectparkapi.exception.CpfUniqueViolationException;
import com.marcot.projectparkapi.exception.EntityNotFoundException;
import com.marcot.projectparkapi.jwt.JwtUserDetails;
import com.marcot.projectparkapi.repository.CustomerRepository;
import com.marcot.projectparkapi.repository.projection.CustomerProjection;
import com.marcot.projectparkapi.web.dto.CustomerCreateDto;
import com.marcot.projectparkapi.web.dto.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserAccountService userService;


    @Transactional
    public Customer createCustomer(CustomerCreateDto dto) {
        Customer customer = CustomerMapper.toCustomer(dto);
        // Get the authenticated user details
        JwtUserDetails userDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        customer.setUserAccount(userService.getById(userDetails.getId()));
        try {
            return customerRepository.save(customer);
        } catch ( DataIntegrityViolationException ex) {
            throw new CpfUniqueViolationException(
                    String.format("CPF '%s' cannot be registered, it already exists in the system", customer.getCpf())
            );
        }

    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Customer id=%s not found in system", id))
        );
    }

    @Transactional(readOnly = true)
    public Page<CustomerProjection> getAllCustomers(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Access Denied");
        }
        return customerRepository.findAllPageable(pageable);
    }

    @Transactional(readOnly = true)
    public Customer findByUserId(Long id) {
            return customerRepository.findByUserAccountId(id);
    }

    @Transactional(readOnly = true)
    public Customer findByCpf(String cpf) {
        return customerRepository.findByCpf(cpf).orElseThrow(
                () -> new EntityNotFoundException(String.format("Customer with CPF '%s' not found", cpf))
        );
    }

}

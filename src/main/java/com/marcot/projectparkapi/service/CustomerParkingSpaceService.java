package com.marcot.projectparkapi.service;


import com.marcot.projectparkapi.entity.CustomerParkingSpace;
import com.marcot.projectparkapi.exception.EntityNotFoundException;
import com.marcot.projectparkapi.repository.CustomerParkingSpaceRepository;
import com.marcot.projectparkapi.repository.projection.CustomerParkingSpaceProjection;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CustomerParkingSpaceService {

    private final CustomerParkingSpaceRepository repository;

    @Transactional
    public CustomerParkingSpace save(CustomerParkingSpace customerParkingSpace) {
        return repository.save(customerParkingSpace);
    }

    @Transactional(readOnly = true)
    public CustomerParkingSpace findByReceiptNumber(String receiptNumber) {
        return repository.findByReceiptNumberAndExitTimeIsNull(receiptNumber).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Receipt number '%s' not found in the system or check-out already completed", receiptNumber)
                )
        );
    }

    @Transactional(readOnly = true)
    public long getTotalParkingTimesByCustomerCpf(String cpf) {
        return repository.countByCustomerCpfAndExitTimeIsNotNull(cpf);
    }

    @Transactional(readOnly = true)
    public Page<CustomerParkingSpaceProjection> findAllByCustomerCpf(String cpf, Pageable pageable) {
        return repository.findAllByCustomerCpf(cpf, pageable);
    }

    @Transactional(readOnly = true)
    public Page<CustomerParkingSpaceProjection> findAllByUserAccountId(Long id, Pageable pageable) {
        return repository.findAllByCustomerUserAccountId(id, pageable);
    }
}

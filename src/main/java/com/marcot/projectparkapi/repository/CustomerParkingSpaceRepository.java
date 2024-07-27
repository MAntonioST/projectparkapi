package com.marcot.projectparkapi.repository;


import com.marcot.projectparkapi.entity.CustomerParkingSpace;
import com.marcot.projectparkapi.repository.projection.CustomerParkingSpaceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CustomerParkingSpaceRepository extends JpaRepository<CustomerParkingSpace, Long> {
    Optional<CustomerParkingSpace> findByReceiptNumberAndExitTimeIsNull(String receiptNumber);

    long countByCustomerCpfAndExitTimeIsNotNull(String cpf);

    Page<CustomerParkingSpaceProjection> findAllByCustomerCpf(String cpf, Pageable pageable);

    Page<CustomerParkingSpaceProjection> findAllByCustomerUserAccountId(Long id, Pageable pageable);
}
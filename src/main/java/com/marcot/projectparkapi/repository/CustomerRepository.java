package com.marcot.projectparkapi.repository;

import com.marcot.projectparkapi.entity.Customer;
import com.marcot.projectparkapi.repository.projection.CustomerProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

    @Query("SELECT c FROM Customer c")
    Page<CustomerProjection> findAllPageable(Pageable pageable);

    Customer findByUserAccountId(Long id);

    Optional<Customer> findByCpf(String cpf);
}

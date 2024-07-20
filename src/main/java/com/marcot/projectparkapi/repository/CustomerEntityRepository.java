package com.marcot.projectparkapi.repository;

import com.marcot.projectparkapi.entity.CustomerEntity;
import com.marcot.projectparkapi.repository.projection.CustomerProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerEntityRepository extends JpaRepository<CustomerEntity,Long> {

    @Query("SELECT c FROM CustomerEntity c")
    Page<CustomerProjection> findAllPageable(Pageable pageable);
}

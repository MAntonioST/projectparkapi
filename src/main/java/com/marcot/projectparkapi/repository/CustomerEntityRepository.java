package com.marcot.projectparkapi.repository;

import com.marcot.projectparkapi.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerEntityRepository extends JpaRepository<CustomerEntity,Long> {

}

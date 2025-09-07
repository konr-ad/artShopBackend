package com.artshop.backend.repositories;

import com.artshop.backend.models.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findAllByEmail(String email, Pageable pageable);

    List<Customer> findAllByEmail(String email);

    Optional<Customer>findByEmail(String email);

    Optional<Customer> findFirstByEmailOrderByIdDesc(String email);

    boolean existsByEmail(String email);
}
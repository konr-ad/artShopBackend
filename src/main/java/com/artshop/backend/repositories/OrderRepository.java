package com.artshop.backend.repositories;

import com.artshop.backend.models.payu.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"customer", "items"})
    Page<Order> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "items"})
    Optional<Order> findById(Long id);

    @EntityGraph(attributePaths = {"customer", "items"})
    Page<Order> findByContactEmailIgnoreCase(String contactEmail, Pageable pageable);

    Optional<Order> findByExtOrderId(String extOrderId);

    Optional<Order> findByPayuOrderId(String payuOrderId);

    Page<Order> findAllByCustomerId(Long customerId, Pageable pageable);
}
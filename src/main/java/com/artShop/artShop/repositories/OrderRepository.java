package com.artShop.artShop.repositories;

import com.artShop.artShop.models.payu.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"customer", "paintings"})
    List<Order> findAll(); // This will ensure that customer and paintings are fetched along with orders
}

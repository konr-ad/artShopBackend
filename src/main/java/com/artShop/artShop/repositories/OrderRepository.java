package com.artShop.artShop.repositories;

import com.artShop.artShop.models.payu.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

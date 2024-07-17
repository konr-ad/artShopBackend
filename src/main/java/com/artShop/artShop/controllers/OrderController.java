package com.artShop.artShop.controllers;
import com.artShop.artShop.models.payu.Order;
import com.artShop.artShop.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order processedOrder = orderService.processOrder(order);
        return ResponseEntity.ok(processedOrder);
    }
}


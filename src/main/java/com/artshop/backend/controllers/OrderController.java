package com.artshop.backend.controllers;

import com.artshop.backend.models.payu.Order;
import com.artshop.backend.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createOrder(@RequestBody Order order) {
        Order processed = orderService.processOrder(order);
        Map<String, String> response = new HashMap<>();
        response.put("redirectUri", processed.getRedirectUri());
        return ResponseEntity.ok(response);
    }

//    @GetMapping
//    public ResponseEntity<List<Order>> getOrders() {
//        return ResponseEntity.ok(orderService.getAllOrders());
//    }
}

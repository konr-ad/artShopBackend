package com.artshop.backend.controllers;

import com.artshop.backend.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/payu")
@RequiredArgsConstructor
@Slf4j
public class PayuNotifyController {
    private final OrderRepository orderRepository;

    @PostMapping("/notify")
    public ResponseEntity<Void> notify(@RequestBody Map<String,Object> payload) {
        log.info("PayU notify: {}", payload);
        // TODO: wyciągnij orderId / extOrderId ze struktury PayU i uaktualnij status
        return ResponseEntity.ok().build();
    }
}
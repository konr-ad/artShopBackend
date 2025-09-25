package com.artshop.backend.controllers;

import com.artshop.backend.Utils.IpUtil;
import com.artshop.backend.api.dto.CreateOrderRequest;
import com.artshop.backend.services.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createOrder(
            @RequestBody CreateOrderRequest req,
            HttpServletRequest http
    ) {
        String clientIp = IpUtil.clientIp(http);
        var processed = orderService.processOrder(req, clientIp);
        return ResponseEntity.ok(Map.of(
                "redirectUri", processed.getRedirectUri(),
                "orderId", processed.getId().toString()
        ));
    }
}
package com.artshop.backend.controllers;

import com.artshop.backend.api.dto.PublicOrderStatusDto;
import com.artshop.backend.models.payu.Order;
import com.artshop.backend.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/public/orders")
@RequiredArgsConstructor
public class PublicOrderController {

    private final OrderService orderService;

    @GetMapping("/{extOrderId}/status")
    public ResponseEntity<PublicOrderStatusDto> getOrderStatus(@PathVariable String extOrderId) {
        Optional<Order> order = orderService.findByExtOrderId(extOrderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        PublicOrderStatusDto dto = new PublicOrderStatusDto(
                order.get().getExtOrderId(),
                order.get().getPaymentStatus().name()
        );

        return ResponseEntity.ok(dto);
    }
}

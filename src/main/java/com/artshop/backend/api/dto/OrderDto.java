package com.artshop.backend.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDto(
        Long id,
        String paymentStatus,
        BigDecimal totalAmount,
        String currencyCode,
        String contactEmail,
        Instant createdAt,
        AddressDto shippingAddress,
        AddressDto billingAddress,
        List<OrderItemDto> items
) {}

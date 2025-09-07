package com.artshop.backend.api.dto;

import java.math.BigDecimal;

public record OrderItemDto(
        Long paintingId,
        String paintingName,
        String paintingType,
        int quantity,
        BigDecimal unitPriceAtPurchase,
        BigDecimal lineTotal
) {}
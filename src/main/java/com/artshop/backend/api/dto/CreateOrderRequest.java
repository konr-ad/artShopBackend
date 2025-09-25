package com.artshop.backend.api.dto;


import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        String description,
        String currencyCode,
        String extOrderId,
        String contactEmail,
        BuyerDto buyer,
        AddressDto shippingAddress,
        List<ItemDto> products
) {
    public record BuyerDto(String email, String firstName, String lastName) {}
    public record ItemDto(
            Long paintingId,
            String name,
            String paintingType,
            java.math.BigDecimal unitPrice,
            int quantity
    ) {}
}

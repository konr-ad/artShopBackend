package com.artshop.backend.api.dto;

public record PublicOrderStatusDto(
        String extOrderId,
        String paymentStatus
) {}
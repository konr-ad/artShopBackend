package com.artshop.backend.api.dto;

import java.math.BigDecimal;

public record PaintingListingDto(
        Long id,
        String name,
        String type,
        String state,
        BigDecimal price,
        String thumbnailUrl
) {}

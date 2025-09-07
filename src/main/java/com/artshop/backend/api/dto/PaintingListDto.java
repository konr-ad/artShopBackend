package com.artshop.backend.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record PaintingListDto(
        Long id,
        String name,
        String type,
        String state,
        BigDecimal price,
        String thumbnailUrl
) {}

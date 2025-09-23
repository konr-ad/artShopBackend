package com.artshop.backend.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record PaintingDetailsDto(
        Long id,
        String name,
        String type,
        String state,
        BigDecimal price,
        String descriptionPl,
        String descriptionEn,
        int quantity,
        List<MediaFileDto> media
) {}
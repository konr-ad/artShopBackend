package com.artshop.backend.api.dto;

import com.artshop.backend.enums.EPaintingState;
import com.artshop.backend.enums.EPaintingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
        * Trafia jako JSON w części multipart @RequestPart("meta")
 */
public record CreatePaintingRequest(
        @NotBlank String name,
        @NotNull EPaintingType type,
        EPaintingState state,
        @NotNull @Positive BigDecimal price,
        String descriptionPl,
        String descriptionEn,
        Integer quantity
) {}
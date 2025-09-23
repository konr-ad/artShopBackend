package com.artshop.backend.api.dto;

public record MediaFileDto(
        String url,
        String type,
        boolean isPrimary,
        int sortOrder,
        String originalFilename
) {}

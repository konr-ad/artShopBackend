package com.artshop.backend.api.dto;

public record CustomerDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String country,
        String state,
        String address,
        String apartmentNumber,
        String city,
        String zip
) {}
package com.artshop.backend.api.dto;

public record AddressDto(
        String street,
        String apartmentNumber,
        String city,
        String state,
        String zip,
        String country
) {}
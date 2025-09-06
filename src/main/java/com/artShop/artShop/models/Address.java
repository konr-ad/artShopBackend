package com.artShop.artShop.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Address {
    @Column(length=255, nullable=false) private String street;
    @Column(length=64)  private String apartmentNumber;
    @Column(length=255, nullable=false) private String city;
    @Column(length=255) private String state;
    @Column(length=16)  private String zip;
    @Column(length=255, nullable=false) private String country;
}
package com.artShop.models.entity;

import com.artShop.models.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "customer")
public class Customer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String email;

    @Column(length = 255)
    private String firstName;

    @Column(length = 255)
    private String lastName;

    @Column(length = 255)
    private String country;

    @Column(length = 255)
    private String state;

    @Column(length = 255)
    private String address;

    @Column(length = 255)
    private String apartmentNumber;

    @Column(length = 255)
    private String city;

    @Column(length = 255)
    private String zip;
}

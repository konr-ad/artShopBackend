package com.artShop.artShop.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "painting")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Painting {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    Long id;

    String type;
    int length;
    int width;
    BigDecimal price;
}

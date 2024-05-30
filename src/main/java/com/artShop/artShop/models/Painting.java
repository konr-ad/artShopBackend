package com.artShop.artShop.models;

import com.artShop.artShop.enums.PaintingState;
import com.artShop.artShop.enums.PaintingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Entity
@Table(name = "painting")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Painting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Type is required")
    private PaintingType type;
    @NotNull(message = "State is required")
    private PaintingState state;
    @Min(value = 1, message = "Length should be at least 1")
    private int length;
    @Min(value = 1, message = "Width should be at least 1")
    private int width;
    @Positive(message = "Price should be positive")
    private double price;

    @Lob
    private byte[] image;
}

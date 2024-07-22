package com.artShop.artShop.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Entity
@Table(name = "additional_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required")
    private String name;

    @Lob
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "painting_id")
    private Painting painting;
}

package com.artShop.artShop.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"image"})
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
    @JsonIgnoreProperties("additionalImages")
    private Painting painting;
}

package com.artshop.backend.controllers;

import com.artshop.backend.api.dto.CreatePaintingRequest;
import com.artshop.backend.api.dto.PaintingDetailsDto;
import com.artshop.backend.api.mapper.PaintingMapper;
import com.artshop.backend.models.entity.Painting;
import com.artshop.backend.repositories.PaintingRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/paintings")
@RequiredArgsConstructor
public class AdminPaintingController {

    private final PaintingRepository paintingRepo;
    private final PaintingMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PaintingDetailsDto create(@Valid @RequestBody CreatePaintingRequest req) {
        Painting p = new Painting();
        p.setName(req.name());
        p.setType(req.type());
        p.setState(req.state());
        p.setPrice(req.price());
        p.setDescription(req.description());
        p.setQuantity(1);

        Painting saved = paintingRepo.save(p);
        return mapper.toDetailsDto(saved);
    }
}
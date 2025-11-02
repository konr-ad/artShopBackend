// src/main/java/com/artshop/backend/controllers/AdminPaintingController.java
package com.artshop.backend.controllers;

import com.artshop.backend.api.dto.CreatePaintingRequest;
import com.artshop.backend.api.dto.PaintingDetailsDto;
import com.artshop.backend.api.dto.PaintingListingDto;
import com.artshop.backend.api.mapper.PaintingMapper;
import com.artshop.backend.enums.EMediaImageType;
import com.artshop.backend.models.entity.Painting;
import com.artshop.backend.models.media.MediaFile;
import com.artshop.backend.repositories.MediaFileRepository;
import com.artshop.backend.repositories.PaintingRepository;
import com.artshop.backend.services.MediaStorage;
import com.artshop.backend.services.PaintingService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/admin/paintings")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
@RequiredArgsConstructor
public class AdminPaintingController {

    private final PaintingRepository paintingRepo;
    private final MediaFileRepository mediaRepo;
    private final PaintingMapper mapper;
    private final MediaStorage storage;
    private final PaintingService paintingService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public PaintingDetailsDto createWithMedia(
            @RequestPart("meta") @Valid CreatePaintingRequest meta,
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam(value = "primaryIndex", required = false) Integer primaryIndex
    ) throws Exception {
        // 1) Utwórz i zapisz Painting
        Painting p = new Painting();
        p.setName(meta.name());
        p.setType(meta.type());
        p.setState(meta.state());
        p.setPrice(meta.price());
        p.setDescriptionPl(meta.descriptionPl());
        p.setDescriptionEn(meta.descriptionEn());
        p.setQuantity(meta.quantity() != null ? meta.quantity() : 1);

        Painting saved = paintingRepo.save(p);

        // 2) Upload mediów (opcjonalnie)
        if (files != null && !files.isEmpty()) {
            int prim = (primaryIndex != null && primaryIndex >= 0 && primaryIndex < files.size()) ? primaryIndex : 0;

            for (int i = 0; i < files.size(); i++) {
                MultipartFile f = files.get(i);

                // zapis fizyczny + URL
                String url = storage.savePaintingFile(saved.getId(), f);

                MediaFile mf = new MediaFile();
                mf.setPainting(saved);
                mf.setUrl(url);
                mf.setType(detectImageType(f)); // JPEG/PNG itp.
                mf.setPrimary(i == prim);
                mf.setSortOrder(i);
                mf.setOriginalFilename(f.getOriginalFilename());

                mediaRepo.save(mf);
            }
        }

        // 3) Wróć DTO z mediami
        // jeśli masz metodę repo dociągającą media, użyj jej; w prostym wariancie mapper na 'saved'
        return mapper.toDetailsDto(saved);
    }

    private EMediaImageType detectImageType(MultipartFile f) {
        String ct = Optional.ofNullable(f.getContentType()).orElse("").toLowerCase();
        return ct.contains("png") ? EMediaImageType.PNG : EMediaImageType.JPEG;
    }

    @GetMapping
    public List<PaintingListingDto>getAllPaintings() {
        return paintingService.findAll().stream()
                .map(mapper::toListDto)
                .toList();
    }

    @PostMapping("/lock")
    public ResponseEntity<Void> lock(@RequestBody List<Long> ids) {
        paintingService.lockPaintings(ids);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/unlock")
    public ResponseEntity<Void> unlock(@RequestBody List<Long> ids) {
        paintingService.unLockPaintings(ids);
        return ResponseEntity.noContent().build();
    }
}

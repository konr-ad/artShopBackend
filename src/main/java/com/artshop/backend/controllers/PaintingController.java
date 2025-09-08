package com.artshop.backend.controllers;

import com.artshop.backend.api.dto.PaintingDetailsDto;
import com.artshop.backend.api.dto.PaintingListDto;
import com.artshop.backend.enums.EPaintingState;
import com.artshop.backend.enums.EPaintingType;
import com.artshop.backend.services.PaintingService;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/paintings")
@RequiredArgsConstructor
@Validated
public class PaintingController {

    private final PaintingService paintingService;

    // GET /api/paintings?type=ABSTRACT&state=AVAILABLE&minPrice=100&maxPrice=1000&q=monet&page=0&size=20&sort=price,asc
    @GetMapping
    public Page<PaintingListDto> list(@RequestParam(required = false) EPaintingType type,
                                      @RequestParam(required = false) EPaintingState state,
                                      @RequestParam(required = false) @PositiveOrZero BigDecimal minPrice,
                                      @RequestParam(required = false) @PositiveOrZero BigDecimal maxPrice,
                                      @RequestParam(required = false, name = "q") String query,
                                      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
        }
        return paintingService.list(type, state, minPrice, maxPrice, query, pageable);
    }

    // GET /api/paintings/{id}
    @GetMapping("/{id}")
    public PaintingDetailsDto details(@PathVariable Long id) {
        return paintingService.getDetails(id);
    }
}

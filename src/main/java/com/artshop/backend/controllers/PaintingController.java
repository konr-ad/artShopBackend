package com.artshop.backend.controllers;

import com.artshop.backend.api.dto.PaintingDetailsDto;
import com.artshop.backend.api.dto.PaintingListingDto;
import com.artshop.backend.enums.EPaintingState;
import com.artshop.backend.enums.EPaintingType;
import com.artshop.backend.services.PaintingService;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/paintings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PaintingController {

    private final PaintingService paintingService;

    // GET /api/paintings?type=ABSTRACT&state=AVAILABLE&minPrice=100&maxPrice=1000&q=monet&page=0&size=20&sort=price,asc
    @GetMapping
    public Page<PaintingListingDto> list(@RequestParam(required = false) EPaintingType type,
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

    @PostMapping("/lock")
    public ResponseEntity<List<Long>> lock(@RequestBody List<Long> ids) {
        log.info("Zablokowano obrazy" + ids);
        return ResponseEntity.ok(List.of(100000000L));
    }

    @PostMapping("/unlock")
    public ResponseEntity<Void> unlock(@RequestBody List<Long> ids) {
        paintingService.lockPaiting(ids.getFirst());
        log.info("Odblokowano obrazy");
        return ResponseEntity.ok().build();
    }
}

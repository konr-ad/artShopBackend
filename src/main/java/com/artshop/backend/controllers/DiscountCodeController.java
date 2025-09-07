package com.artshop.backend.controllers;

import com.artshop.backend.api.dto.discount.DiscountCodeDto;
import com.artshop.backend.api.dto.discount.DiscountCodeRequest;
import com.artshop.backend.api.dto.discount.DiscountCodeResponse;
import com.artshop.backend.api.mapper.DiscountCodeMapper;
import com.artshop.backend.models.entity.DiscountCode;
import com.artshop.backend.services.DiscountCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discountcodes")
@RequiredArgsConstructor
public class DiscountCodeController {

    private final DiscountCodeService discountCodeService;
    private final DiscountCodeMapper discountCodeMapper;

    // Public: walidacja kodu w koszyku
    @PostMapping("/validate")
    public DiscountCodeResponse validate(@RequestBody DiscountCodeRequest request) {
        return discountCodeService.validateDiscountCode(request);
    }

    // Admin: listing wszystkich kodów (DTO encji)
    @GetMapping
    public List<DiscountCodeDto> listAll() {
        return discountCodeService.findAll().stream()
                .map(discountCodeMapper::toDto)
                .toList();
    }

    // Admin: dodanie kodu (MVP – encja; można dodać osobne CreateDto)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiscountCodeDto add(@RequestBody DiscountCode discountCode) {
        return discountCodeMapper.toDto(discountCodeService.add(discountCode));
    }

    // Admin: kasowanie jednego
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOne(@PathVariable Long id) {
        discountCodeService.deleteById(id);
    }

    // Admin: kasowanie wielu
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMany(@RequestBody List<Long> ids) {
        discountCodeService.deleteByIds(ids);
    }
}

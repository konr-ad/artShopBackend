package com.artshop.backend.controllers;

import com.artshop.backend.api.dto.discount.DiscountCodeCreateRequest;
import com.artshop.backend.api.dto.discount.DiscountCodeDto;
import com.artshop.backend.api.dto.discount.DiscountCodeRequest;
import com.artshop.backend.api.dto.discount.DiscountCodeResponse;
import com.artshop.backend.api.mapper.DiscountCodeMapper;
import com.artshop.backend.models.entity.DiscountCode;
import com.artshop.backend.services.DiscountCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discountcodes")
@RequiredArgsConstructor
public class DiscountCodeController {

    private final DiscountCodeService discountCodeService;
    private final DiscountCodeMapper discountCodeMapper;

    @PostMapping("/validate")
    public DiscountCodeResponse validate(@RequestBody DiscountCodeRequest request) {
        return discountCodeService.validateDiscountCode(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<DiscountCodeDto> listAll() {
        return discountCodeService.findAll().stream()
                .map(discountCodeMapper::toDto)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public DiscountCodeDto add(@RequestBody DiscountCodeCreateRequest req) {
        return discountCodeMapper.toDto(discountCodeService.add(req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOne(@PathVariable Long id) {
        discountCodeService.deleteById(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMany(@RequestBody List<Long> ids) {
        discountCodeService.deleteByIds(ids);
    }
}

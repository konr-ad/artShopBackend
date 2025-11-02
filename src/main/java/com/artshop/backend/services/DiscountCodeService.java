package com.artshop.backend.services;

import com.artshop.backend.api.dto.discount.DiscountCodeCreateRequest;
import com.artshop.backend.api.dto.discount.DiscountCodeDto;
import com.artshop.backend.api.dto.discount.DiscountCodeRequest;
import com.artshop.backend.api.dto.discount.DiscountCodeResponse;
import com.artshop.backend.api.mapper.DiscountCodeMapper;
import com.artshop.backend.exception.CodeNotFoundException;
import com.artshop.backend.models.entity.DiscountCode;
import com.artshop.backend.repositories.DiscountCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiscountCodeService {

    private final DiscountCodeRepository discountCodeRepository;
    private final DiscountCodeMapper discountCodeMapper;

    public DiscountCodeResponse validateDiscountCode(DiscountCodeRequest req) {
        DiscountCode dc = discountCodeRepository.findByCode(req.code())
                .orElseThrow(() -> new CodeNotFoundException("Code not found"));

        // aktywny?
        if (!dc.isActive()) {
            return DiscountCodeResponse.invalid("Discount code is inactive");
        }

        // okno ważności (jeśli ustawione)
        LocalDate today = LocalDate.now();
        if (dc.getValidFrom() != null && dc.getValidFrom().isAfter(today)) {
            return DiscountCodeResponse.invalid("Discount code not yet valid");
        }
        if (dc.getValidTo() != null && dc.getValidTo().isBefore(today)) {
            return DiscountCodeResponse.invalid("Discount code expired");
        }

        // limit użyć (0 = bez limitu)
        if (dc.getUsageLimit() > 0 && dc.getTimesUsed() >= dc.getUsageLimit()) {
            return DiscountCodeResponse.invalid("Discount code usage limit reached");
        }

        // minimalna wartość zamówienia (jeśli ustawiona i przekazano orderValue)
        if (dc.getMinimumOrderValue() != null && req.orderValue() != null) {
            BigDecimal ov = req.orderValue();
            if (ov.compareTo(dc.getMinimumOrderValue()) < 0) {
                return DiscountCodeResponse.invalid("Order value below minimum");
            }
        }

        return new DiscountCodeResponse(true, "OK", dc.getDiscountValue(), dc.getDiscountType());
    }

    public void incrementUsage(DiscountCode dc) {
        dc.setTimesUsed(dc.getTimesUsed() + 1);
        discountCodeRepository.save(dc);
    }

    public DiscountCode add(DiscountCodeCreateRequest req) {
        DiscountCode entity = discountCodeMapper.toEntity(req);

        LocalDate today = LocalDate.now();
        boolean isActive = true;

        LocalDate from = req.validFrom();
        LocalDate to = req.validTo();

        if (from != null && today.isBefore(from)) {
            isActive = false; // jeszcze nie obowiązuje
        }
        if (to != null && today.isAfter(to)) {
            isActive = false; // już po terminie
        }

        entity.setActive(isActive);
        log.info("Discount code '{}' active={} (validFrom={}, validTo={}, today={})",
                entity.getCode(), isActive, from, to, today);

        return discountCodeRepository.save(entity);
    }
    public List<DiscountCode> findAll() {
        LocalDate today = LocalDate.now();

        return discountCodeRepository.findAll()
                .stream()
                .peek(dc -> {
                    boolean isValidFrom = dc.getValidFrom() == null || !dc.getValidFrom().isAfter(today);
                    boolean isValidTo = dc.getValidTo() == null || !dc.getValidTo().isBefore(today);
                    dc.setActive(isValidFrom && isValidTo);
                })
                .collect(Collectors.toList());
    }
    public void deleteById(Long id) { discountCodeRepository.deleteById(id); }
    public void deleteByIds(List<Long> ids) { discountCodeRepository.deleteAllById(ids); }
}
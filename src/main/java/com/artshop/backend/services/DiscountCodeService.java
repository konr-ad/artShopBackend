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
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
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

        return new DiscountCodeResponse(true, "OK", dc.getDiscountValue());
    }

    public void incrementUsage(DiscountCode dc) {
        dc.setTimesUsed(dc.getTimesUsed() + 1);
        discountCodeRepository.save(dc);
    }

    public DiscountCode add(DiscountCodeCreateRequest req) {
        var entity = discountCodeMapper.toEntity(req);
        return discountCodeRepository.save(entity);
    }
    public List<DiscountCode> findAll() { return discountCodeRepository.findAll(); }
    public void deleteById(Long id) { discountCodeRepository.deleteById(id); }
    public void deleteByIds(List<Long> ids) { discountCodeRepository.deleteAllById(ids); }
}
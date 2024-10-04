package com.artShop.artShop.services;

import com.artShop.artShop.exception.EntityNotFoundException;
import com.artShop.artShop.models.DiscountCode;
import com.artShop.artShop.models.discountCodeDto.DiscountCodeRequest;
import com.artShop.artShop.models.discountCodeDto.DiscountCodeResponse;
import com.artShop.artShop.repositories.DiscountCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiscountCodeService {


    private final DiscountCodeRepository discountCodeRepository;

    @Autowired
    public DiscountCodeService(DiscountCodeRepository discountCodeRepository) {
        this.discountCodeRepository = discountCodeRepository;
    }

    public DiscountCodeResponse validateDiscountCode(DiscountCodeRequest request) {
        DiscountCode discountCode = discountCodeRepository.findByCode(request.getCode())
                .orElseThrow(() -> new EntityNotFoundException("Code not found"));

//        if (!discountCode.isActive()) {
//            return new DiscountCodeResponse(false, "Discount code is inactive", BigDecimal.ZERO);
//        }
//
//        if (discountCode.getExpirationDate() != null && discountCode.getExpirationDate().isBefore(LocalDateTime.now())) {
//            return new DiscountCodeResponse(false, "Discount code has expired", BigDecimal.ZERO);
//        }
//
//        if (request.getOrderValue().compareTo(discountCode.getMinimumOrderValue()) < 0) {
//            return new DiscountCodeResponse(false, "Order value is less than the minimum required for this discount code", BigDecimal.ZERO);
//        }
//
//        if (discountCode.getTimesUsed() >= discountCode.getUsageLimit()) {
//            return new DiscountCodeResponse(false, "Discount code usage limit reached", BigDecimal.ZERO);
//        }
//
//        return new DiscountCodeResponse(true, "Discount code is valid", discountCode.getDiscountValue());
        return null;
    }

    public void incrementUsage(DiscountCode discountCode) {
        discountCode.setTimesUsed(discountCode.getTimesUsed() + 1);
        discountCodeRepository.save(discountCode);
    }

    public DiscountCode addDiscountCode(DiscountCode discountCode) {
        return discountCodeRepository.save(discountCode);
    }

    public List<DiscountCode> findAll() {
        return discountCodeRepository.findAll();
    }

    public void deleteById(Long id) {
        discountCodeRepository.deleteById(id);
    }
}
package com.artShop.artShop.controllers;

import com.artShop.artShop.Utils.ValidationUtils;
import com.artShop.artShop.models.DiscountCode;
import com.artShop.artShop.models.discountCodeDto.DiscountCodeRequest;
import com.artShop.artShop.models.discountCodeDto.DiscountCodeResponse;
import com.artShop.artShop.services.DiscountCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/discountcodes")
public class DiscountCodeController {

    private final DiscountCodeService discountCodeService;

    public DiscountCodeController(DiscountCodeService discountCodeService) {
        this.discountCodeService = discountCodeService;
    }

    @GetMapping
    public ResponseEntity<?> getAllDiscountCodes() {
        List<DiscountCode> discountCodes = discountCodeService.findAll();
        return new ResponseEntity<>(discountCodes, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addDiscountCode(@RequestBody DiscountCode discountCode, BindingResult result) {
        ResponseEntity<?> errorMap = ValidationUtils.getResponseEntity(result);
        if (errorMap != null) return errorMap;
        DiscountCode newDiscountCode = discountCodeService.addDiscountCode(discountCode);
        return new ResponseEntity<>(newDiscountCode, HttpStatus.CREATED);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateDiscountCode(@RequestBody DiscountCodeRequest request, BindingResult result) {
        ResponseEntity<?> errorMap = ValidationUtils.getResponseEntity(result);
        if (errorMap != null) return errorMap;
        DiscountCodeResponse response = discountCodeService.validateDiscountCode(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiscountCode(@PathVariable Long id) {
        discountCodeService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

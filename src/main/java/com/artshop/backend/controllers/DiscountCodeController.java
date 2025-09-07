package com.artshop.backend.controllers;

import com.artshop.backend.Utils.ValidationUtils;
import com.artshop.backend.api.dto.DiscountCodeDto;
import com.artshop.backend.models.entity.DiscountCode;
import com.artshop.backend.services.DiscountCodeService;
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
    public ResponseEntity<?> validateDiscountCode(@RequestBody DiscountCodeDto request, BindingResult result) {
        ResponseEntity<?> errorMap = ValidationUtils.getResponseEntity(result);
        if (errorMap != null) return errorMap;
        DiscountCodeDto response = discountCodeService.validateDiscountCode(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiscountCode(@PathVariable Long id) {
        discountCodeService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAllDiscountCodesById(@RequestBody List<Long> ids) {
        discountCodeService.deleteById(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

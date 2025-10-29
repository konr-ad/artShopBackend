package com.artshop.backend.controllers;


import com.artshop.backend.api.dto.CustomerDto;
import com.artshop.backend.api.dto.OrderDto;
import com.artshop.backend.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Page<CustomerDto> getAllCustomers(Pageable pageable) {
        return customerService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public CustomerDto getCustomerById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @GetMapping("/{id}/orders")
    public Page<OrderDto> getCustomerOrders(@PathVariable Long id, Pageable pageable) {
        return customerService.findOrdersByCustomerId(id, pageable);
    }
}

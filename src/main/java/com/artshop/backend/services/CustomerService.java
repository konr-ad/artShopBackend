package com.artshop.backend.services;

import com.artshop.backend.api.dto.CustomerDto;
import com.artshop.backend.api.dto.OrderDto;
import com.artshop.backend.api.mapper.CustomerMapper;
import com.artshop.backend.api.mapper.OrderMapper;
import com.artshop.backend.models.entity.Customer;
import com.artshop.backend.repositories.CustomerRepository;
import com.artshop.backend.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService  {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final CustomerMapper customerMapper;
    private final OrderMapper orderMapper;

    public Page<CustomerDto> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable).map(customerMapper::toDto);
    }

    public CustomerDto findById(Long id) {
        var customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));
        return customerMapper.toDto(customer);
    }

    public Page<OrderDto> findOrdersByCustomerId(Long customerId, Pageable pageable) {
        return orderRepository.findAllByCustomerId(customerId, pageable)
                .map(orderMapper::toDto);
    }
}


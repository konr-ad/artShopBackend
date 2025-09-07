package com.artshop.backend.api.mapper;

import com.artshop.backend.api.dto.CustomerDto;
import com.artshop.backend.models.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface CustomerMapper {
    CustomerDto toDto(Customer entity);
}
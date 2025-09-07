package com.artshop.backend.api.mapper;

import com.artshop.backend.api.dto.AddressDto;
import com.artshop.backend.models.entity.Address;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface AddressMapper {
    AddressDto toDto(Address entity);
    Address toEntity(AddressDto dto);
}

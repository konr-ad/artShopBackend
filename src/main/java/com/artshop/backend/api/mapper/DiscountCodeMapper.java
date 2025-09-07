package com.artshop.backend.api.mapper;

import com.artshop.backend.api.dto.discount.DiscountCodeDto;
import com.artshop.backend.models.entity.DiscountCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface DiscountCodeMapper {

    @Mapping(target = "discountType", expression = "java(entity.getDiscountType().name())")
    @Mapping(target = "isActive", source = "active")
    DiscountCodeDto toDto(DiscountCode entity);
}
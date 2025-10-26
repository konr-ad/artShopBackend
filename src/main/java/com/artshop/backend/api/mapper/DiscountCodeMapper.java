package com.artshop.backend.api.mapper;

import com.artshop.backend.api.dto.discount.DiscountCodeCreateRequest;
import com.artshop.backend.api.dto.discount.DiscountCodeDto;
import com.artshop.backend.models.entity.DiscountCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface DiscountCodeMapper {

    // ENTITY -> DTO
    @Mapping(target = "discountType", expression = "java(entity.getDiscountType().name())")
    @Mapping(target = "isActive", source = "active")
    DiscountCodeDto toDto(DiscountCode entity);

    // CREATE REQUEST -> ENTITY
    @Mapping(target = "id", ignore = true)
    @Mapping(
            target = "discountType",
            expression = "java(com.artshop.backend.enums.EDiscountType.valueOf(req.discountType()))"
    )
    @Mapping(target = "active", source = "isActive")
    @Mapping(target = "timesUsed", constant = "0")
    DiscountCode toEntity(DiscountCodeCreateRequest req);
}
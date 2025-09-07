package com.artshop.backend.api.mapper;

import com.artshop.backend.api.dto.OrderItemDto;
import com.artshop.backend.models.payu.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = MapStructConfig.class)
public interface OrderItemMapper {

    @Mappings({
            @Mapping(target = "paintingName", source = "paintingNameSnapshot"),
            @Mapping(target = "paintingType", expression = "java(e.getPaintingTypeSnapshot().name())"),
            @Mapping(target = "lineTotal", expression = "java(e.lineTotal())")
    })
    OrderItemDto toDto(OrderItem e);
}
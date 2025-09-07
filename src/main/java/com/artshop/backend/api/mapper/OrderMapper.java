package com.artshop.backend.api.mapper;

import com.artshop.backend.api.dto.OrderDto;
import com.artshop.backend.models.payu.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = MapStructConfig.class, uses = { AddressMapper.class, OrderItemMapper.class })
public interface OrderMapper {

    @Mappings({
            @Mapping(target = "paymentStatus", expression = "java(o.getPaymentStatus().name())")
    })
    OrderDto toDto(Order o);
}
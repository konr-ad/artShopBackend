package com.artshop.backend.api.mapper;

import com.artshop.backend.api.dto.MediaFileDto;
import com.artshop.backend.models.media.MediaFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface MediaFileMapper {

    @Mapping(target = "type", expression = "java(entity.getType().name())")
    @Mapping(target = "isPrimary", source = "primary")
    MediaFileDto toDto(MediaFile entity);
}
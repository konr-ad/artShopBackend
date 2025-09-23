package com.artshop.backend.api.mapper;

import com.artshop.backend.api.dto.PaintingDetailsDto;
import com.artshop.backend.api.dto.PaintingListingDto;
import com.artshop.backend.models.entity.Painting;
import com.artshop.backend.models.media.MediaFile;
import org.mapstruct.*;

import java.util.Comparator;
import java.util.List;

@Mapper(config = MapStructConfig.class, uses = { MediaFileMapper.class })
public interface PaintingMapper {

    @Mappings({
            @Mapping(target = "type", expression = "java(p.getType().name())"),
            @Mapping(target = "state", expression = "java(p.getState() != null ? p.getState().name() : null)"),
            @Mapping(target = "thumbnailUrl", source = "media", qualifiedByName = "primaryUrl")
    })
    PaintingListingDto toListDto(Painting p);

    @Mappings({
            @Mapping(target = "type", expression = "java(p.getType().name())"),
            @Mapping(target = "state", expression = "java(p.getState() != null ? p.getState().name() : null)"),
            @Mapping(target = "descriptionPl", source = "descriptionPl"),
            @Mapping(target = "descriptionEn", source = "descriptionEn")
    })
    PaintingDetailsDto toDetailsDto(Painting p);

    @Named("primaryUrl")
    default String primaryUrl(List<MediaFile> media) {
        if (media == null || media.isEmpty()) return null;
        return media.stream()
                .sorted(Comparator.comparing(MediaFile::isPrimary).reversed()
                        .thenComparingInt(MediaFile::getSortOrder)
                        .thenComparing(MediaFile::getId))
                .map(MediaFile::getUrl)
                .findFirst()
                .orElse(null);
    }
}

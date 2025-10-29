package com.artshop.backend.services;

import com.artshop.backend.api.dto.PaintingDetailsDto;
import com.artshop.backend.api.dto.PaintingListingDto;
import com.artshop.backend.api.mapper.PaintingMapper;
import com.artshop.backend.enums.EPaintingState;
import com.artshop.backend.enums.EPaintingType;
import com.artshop.backend.exception.EntityNotFoundException;
import com.artshop.backend.models.entity.Painting;
import com.artshop.backend.repositories.PaintingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaintingService {

    private final PaintingRepository paintingRepository;
    private final PaintingMapper paintingMapper;

    @Transactional(readOnly = true)
    public Page<PaintingListingDto> list(EPaintingType type,
                                         EPaintingState state,
                                         BigDecimal minPrice,
                                         BigDecimal maxPrice,
                                         String query,
                                         Pageable pageable) {
        Page<Painting> page;

        if (query != null && !query.isBlank()) {
            page = paintingRepository.findAllByNameContainingIgnoreCase(query.trim(), pageable);
        } else if (minPrice != null && maxPrice != null) {
            page = paintingRepository.findAllByPriceBetween(minPrice, maxPrice, pageable);
        } else if (type != null && state != null) {
            page = paintingRepository.findAllByTypeAndState(type, state, pageable);
        } else if (type != null) {
            page = paintingRepository.findAllByType(type, pageable);
        } else if (state != null) {
            page = paintingRepository.findAllByState(state, pageable);
        } else {
            page = paintingRepository.findAll(pageable);
        }

        return page.map(paintingMapper::toListDto);
    }

    @Transactional(readOnly = true)
    public PaintingDetailsDto getDetails(Long id) {
        return paintingRepository.findByIdWithMedia(id)
                .map(paintingMapper::toDetailsDto)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Painting id=" + id + " not found"));
    }

    @Transactional
    public void lockPaintings(List<Long> ids) {
        Painting p = null;
        List<Long> lockedIds = new ArrayList<>();
        for (Long id : ids) {
            p = paintingRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Painting " + id + " not found"));
            if (p.getState() != EPaintingState.AVAILABLE) {
                continue;
            }
            p.setState(EPaintingState.RESERVED);
            paintingRepository.save(p);
            lockedIds.add(id);
        }
        log.info("Followings ids have been locked: {}", lockedIds);
    }

    public void unLockPaintings(List<Long> ids) {
        Painting p = null;
        List<Long> unLockedIds = new ArrayList<>();
        for (Long id : ids) {
            p = paintingRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Painting " + id + " not found"));
            if (p.getState() == EPaintingState.AVAILABLE) {
                continue;
            }
            p.setState(EPaintingState.AVAILABLE);
            paintingRepository.save(p);
            unLockedIds.add(id);
        }
        log.info("Followings ids have been unlocked: {}", unLockedIds);
    }

    public List<Painting> findAll() {
        return paintingRepository.findAll();
    }
}

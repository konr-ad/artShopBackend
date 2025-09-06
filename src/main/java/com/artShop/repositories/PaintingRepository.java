package com.artShop.repositories;

import com.artShop.enums.EPaintingState;
import com.artShop.enums.EPaintingType;
import com.artShop.models.entity.Painting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, Long> {

    Page<Painting> findAll(Pageable pageable);
    Page<Painting> findAllByType(EPaintingType type, Pageable pageable);
    Page<Painting> findAllByState(EPaintingState state, Pageable pageable);
    Page<Painting> findAllByTypeAndState(EPaintingType type, EPaintingState state, Pageable pageable);
    Page<Painting> findAllByPriceBetween(BigDecimal min, BigDecimal max, Pageable pageable);

    @EntityGraph(attributePaths = "media")
    Optional<Painting> findByIdWithMedia(Long id);
}

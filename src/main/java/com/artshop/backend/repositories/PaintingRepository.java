package com.artshop.backend.repositories;

import com.artshop.backend.enums.EPaintingState;
import com.artshop.backend.enums.EPaintingType;
import com.artshop.backend.models.entity.Painting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, Long> {

    Page<Painting> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Painting> findAllByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Painting> findAllByType(EPaintingType type, Pageable pageable);

    Page<Painting> findAllByState(EPaintingState state, Pageable pageable);

    Page<Painting> findAllByTypeAndState(EPaintingType type, EPaintingState state, Pageable pageable);

    // szczegóły z dociągniętymi mediami (do endpointu /{id})
    @Query("select distinct p from Painting p left join fetch p.media where p.id = :id")
    Optional<Painting> findByIdWithMedia(@Param("id") Long id);
}

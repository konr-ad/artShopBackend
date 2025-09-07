package com.artshop.backend.repositories;

import com.artshop.backend.models.entity.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
    Optional<DiscountCode> findByCode(String code);

    @Query("""
   select d from DiscountCode d
    where d.code = :code
      and d.isActive = true
      and (d.validFrom is null or d.validFrom <= :today)
      and (d.validTo   is null or d.validTo   >= :today)
      and (d.usageLimit = 0 or d.timesUsed < d.usageLimit)
""")
    Optional<DiscountCode> findActiveUsableByCode(@Param("code") String code,
                                                  @Param("today") LocalDate today);

    boolean existsByCode(String code);
}

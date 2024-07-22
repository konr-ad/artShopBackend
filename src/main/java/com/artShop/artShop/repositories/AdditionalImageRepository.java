package com.artShop.artShop.repositories;

import com.artShop.artShop.models.AdditionalImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalImageRepository extends JpaRepository<AdditionalImage, Long> {
}

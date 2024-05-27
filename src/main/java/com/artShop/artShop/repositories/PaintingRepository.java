package com.artShop.artShop.repositories;

import com.artShop.artShop.models.Painting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, Long> {
}

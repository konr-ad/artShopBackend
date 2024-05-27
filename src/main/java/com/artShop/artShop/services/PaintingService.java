package com.artShop.artShop.services;

import com.artShop.artShop.models.Painting;
import com.artShop.artShop.repositories.PaintingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaintingService {

    private final PaintingRepository paintingRepository;

    @Autowired
    public PaintingService(PaintingRepository paintingRepository){
        this.paintingRepository = paintingRepository;
    }

    public Painting getPainting(Long id) {
        return paintingRepository.getReferenceById(id);
    }

    public Painting savePainting(Painting painting) {
        return paintingRepository.save(painting);
    }
}

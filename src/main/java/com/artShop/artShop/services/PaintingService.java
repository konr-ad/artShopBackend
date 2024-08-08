package com.artShop.artShop.services;

import com.artShop.artShop.enums.EPaintingState;
import com.artShop.artShop.enums.EPaintingType;
import com.artShop.artShop.models.Painting;
import com.artShop.artShop.repositories.PaintingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class PaintingService {

    private final PaintingRepository paintingRepository;

    @Autowired
    public PaintingService(PaintingRepository paintingRepository){
        this.paintingRepository = paintingRepository;
    }

    public Painting findById(Long id) {
        return paintingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Painting with id: " + id + " not found"));
    }


    public void deleteById(Long id) {
        findById(id);
        paintingRepository.deleteById(id);
    }

    public Painting update(Painting updatedPainting) {
        Painting existingPainting = findById(updatedPainting.getId());
        updatePaintingFields(existingPainting, updatedPainting);
        return paintingRepository.save(existingPainting);
    }

    private void updatePaintingFields(Painting existingPainting, Painting updatedPainting) {
        existingPainting.setState(updatedPainting.getState());
        existingPainting.setType(updatedPainting.getType());
        existingPainting.setPrice(updatedPainting.getPrice());
        existingPainting.setDescription(updatedPainting.getDescription());
    }

    public List<Painting> findAll() {
        return paintingRepository.findAll();
    }

    public Painting createPainting(String type, String name, String description, double price, MultipartFile image) throws IOException {
        Painting painting = new Painting();
        painting.setType(EPaintingType.valueOf(type));
        painting.setName(name);
        painting.setDescription(description);
        painting.setPrice(price);
        painting.setImage(image.getBytes());

        return paintingRepository.save(painting);
    }
}

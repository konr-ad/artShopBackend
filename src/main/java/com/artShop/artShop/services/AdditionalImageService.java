package com.artShop.artShop.services;

import com.artShop.artShop.models.AdditionalImage;
import com.artShop.artShop.models.Painting;
import com.artShop.artShop.repositories.AdditionalImageRepository;
import com.artShop.artShop.repositories.PaintingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdditionalImageService {

    private final AdditionalImageRepository additionalImageRepository;
    private final PaintingService paintingService;

    @Autowired
    public AdditionalImageService(AdditionalImageRepository additionalImageRepository, PaintingService paintingService) {
        this.additionalImageRepository = additionalImageRepository;
        this.paintingService = paintingService;
    }

    public List<AdditionalImage> uploadAdditionalImages(Long paintingId, MultipartFile[] ImageFiles) throws IOException {
        Painting painting = paintingService.findById(paintingId);

        List<AdditionalImage> additionalImages = new ArrayList<>();
        for (MultipartFile image : ImageFiles) {
            AdditionalImage additionalImage = new AdditionalImage();
            additionalImage.setName(image.getOriginalFilename());
            additionalImage.setPainting(painting);
            additionalImage.setImage(image.getBytes());
            additionalImages.add(additionalImage);
        }

        return additionalImageRepository.saveAll(additionalImages);
    }

}

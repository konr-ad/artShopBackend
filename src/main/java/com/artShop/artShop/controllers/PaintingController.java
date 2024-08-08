package com.artShop.artShop.controllers;

import com.artShop.artShop.Utils.ValidationUtils;
import com.artShop.artShop.models.Painting;
import com.artShop.artShop.services.AdditionalImageService;
import com.artShop.artShop.services.PaintingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/paintings")
public class PaintingController {

    private final PaintingService paintingService;
    private final AdditionalImageService additionalImageService;

    @Autowired
    public PaintingController(PaintingService paintingService, AdditionalImageService additionalImageService) {
        this.paintingService = paintingService;
        this.additionalImageService = additionalImageService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPaintings() {
        return new ResponseEntity<>(paintingService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPainting(@PathVariable Long id) {
        return new ResponseEntity<>(paintingService.findById(id), HttpStatus.OK);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createPainting(
            @Valid @RequestParam("type") String type,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestPart("image") MultipartFile image,
            @RequestPart(value = "additionalImages", required = false) MultipartFile[] additionalImages) {
        try {
            Painting createdPainting = paintingService.createPainting(type, name, description, price, image);
            if (additionalImages != null && additionalImages.length > 0) {
                additionalImageService.uploadAdditionalImages(createdPainting.getId(), additionalImages);
            }
            return new ResponseEntity<>(createdPainting, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Image processing failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePainting(@PathVariable Long id) {
        paintingService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> updatePainting(@Valid @RequestParam("painting") Painting painting, @RequestPart(value = "additionalImages", required = false) MultipartFile[] additionalImages, BindingResult result) {
        ResponseEntity<?> errorMap = ValidationUtils.getResponseEntity(result);
        if (errorMap != null) return errorMap;

        try {
            Painting updatedPainting = paintingService.update(painting);

            if (additionalImages != null && additionalImages.length > 0) {
                additionalImageService.uploadAdditionalImages(updatedPainting.getId(), additionalImages);
            }

            return new ResponseEntity<>(updatedPainting, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Image processing failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.artshop.backend.controllers;

import com.artshop.backend.Utils.ValidationUtils;
import com.artshop.backend.models.entity.Painting;
import com.artshop.backend.services.PaintingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/paintings")
public class PaintingController {

    private final PaintingService paintingService;

    @Autowired
    public PaintingController(PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPaintings() {
        return new ResponseEntity<>(paintingService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPainting(@PathVariable Long id) {
        return new ResponseEntity<>(paintingService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/main-image")
    public ResponseEntity<byte[]> getMainImage(@PathVariable Long id) {
        Painting painting = paintingService.findById(id);
        byte[] image = painting.getImage();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
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

            return new ResponseEntity<>(updatedPainting, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Image processing failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

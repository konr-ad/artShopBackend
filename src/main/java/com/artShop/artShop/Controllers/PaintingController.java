package com.artShop.artShop.Controllers;

import com.artShop.artShop.models.Painting;
import com.artShop.artShop.services.PaintingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/paintings")
public class PaintingController {

    private final PaintingService paintingService;

    @Autowired
    public PaintingController(PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPainting(@PathVariable Long id) {
        return new ResponseEntity<>(paintingService.getPainting(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createPainting(@Valid @RequestBody Painting painting, BindingResult result) {
        ResponseEntity<?> errorMap = validationUtils.getResponseEntity(result);
        if (errorMap != null) return errorMap;
    }
}

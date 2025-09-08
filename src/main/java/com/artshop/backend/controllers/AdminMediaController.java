package com.artshop.backend.controllers;

import com.artshop.backend.models.entity.Painting;
import com.artshop.backend.models.media.MediaFile;
import com.artshop.backend.repositories.MediaFileRepository;
import com.artshop.backend.repositories.PaintingRepository;
import com.artshop.backend.services.MediaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/admin/paintings/{id}/media")
@RequiredArgsConstructor
public class AdminMediaController {

    private final PaintingRepository paintingRepo;
    private final MediaFileRepository mediaRepo;
    private final MediaStorage storage;

    public record MediaFileDto(String url, String type, boolean isPrimary, int sortOrder, String originalFilename) {}

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public List<MediaFileDto> upload(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value="primaryIndex", required=false) Integer primaryIndex
    ) throws Exception {
        Painting p = paintingRepo.findById(id).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Painting not found"));

        List<MediaFileDto> out = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile f = files.get(i);
            String url = storage.savePaintingFile(id, f);

            MediaFile mf = new MediaFile();
            mf.setPainting(p);
            mf.setUrl(url);
            mf.setType(f.getContentType() != null && f.getContentType().toLowerCase().contains("png") ? com.artshop.backend.enums.EMediaImageType.PNG : com.artshop.backend.enums.EMediaImageType.JPEG);
            mf.setPrimary(primaryIndex != null ? (i == primaryIndex) : (i == 0)); // domyślnie pierwszy primary
            mf.setSortOrder(i);
            mf.setOriginalFilename(f.getOriginalFilename());

            mediaRepo.save(mf);

            out.add(new MediaFileDto(mf.getUrl(), mf.getType().name(), mf.isPrimary(), mf.getSortOrder(), mf.getOriginalFilename()));
        }
        return out;
    }
}

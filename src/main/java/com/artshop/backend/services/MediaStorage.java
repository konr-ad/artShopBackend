package com.artshop.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.nio.file.*;
import java.io.InputStream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
@Service
public class MediaStorage {

    private final Path baseDir;
    private final String baseUrl;

    public MediaStorage(
            @Value("${app.media.storage-dir:./storage/media}") String storageDir,
            @Value("${app.media.base-url:http://localhost:8080/media}") String baseUrl
    ) {
        this.baseDir = Paths.get(storageDir).toAbsolutePath().normalize();
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public String savePaintingFile(Long paintingId, MultipartFile file) throws Exception {
        String ext = guessExt(file.getOriginalFilename());
        String name = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

        Path dir = baseDir.resolve(String.valueOf(paintingId));
        Files.createDirectories(dir);

        Path target = dir.resolve(name);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, REPLACE_EXISTING);
        }
        return this.baseUrl + "/" + paintingId + "/" + name;
    }

    private static String guessExt(String original) {
        if (original == null) return "";
        int dot = original.lastIndexOf('.');
        return (dot > 0 && dot < original.length() - 1)
                ? original.substring(dot + 1).toLowerCase()
                : "";
    }
}
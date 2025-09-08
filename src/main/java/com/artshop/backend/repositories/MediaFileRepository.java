package com.artshop.backend.repositories;

import com.artshop.backend.models.media.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile,Long> {
}

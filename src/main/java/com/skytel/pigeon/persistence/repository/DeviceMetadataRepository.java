package com.skytel.pigeon.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skytel.pigeon.persistence.models.DeviceMetadata;

@Repository
public interface DeviceMetadataRepository extends JpaRepository<DeviceMetadata, Long> {
   
    List<DeviceMetadata> findByUserId(Long userId);
}

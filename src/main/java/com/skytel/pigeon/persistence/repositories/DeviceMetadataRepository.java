package com.skytel.pigeon.persistence.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.skytel.pigeon.persistence.entities.DeviceMetadata;

public interface DeviceMetadataRepository extends JpaRepository<DeviceMetadata, Long> {

    List<DeviceMetadata> findByUserId(Long userId);
}

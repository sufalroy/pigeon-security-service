package com.skytel.pigeon.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skytel.pigeon.persistence.entities.NewLocationToken;
import com.skytel.pigeon.persistence.entities.UserLocation;

public interface NewLocationTokenRepository extends JpaRepository<NewLocationToken, Long> {

    NewLocationToken findByToken(String token);

    NewLocationToken findByUserLocation(UserLocation userLocation);
}

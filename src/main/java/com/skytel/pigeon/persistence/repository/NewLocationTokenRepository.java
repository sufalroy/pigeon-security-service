package com.skytel.pigeon.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skytel.pigeon.persistence.models.NewLocationToken;
import com.skytel.pigeon.persistence.models.UserLocation;

@Repository
public interface NewLocationTokenRepository extends JpaRepository<NewLocationToken, Long> {

    NewLocationToken findByToken(String token);

    NewLocationToken findByUserLocation(UserLocation userLocation);
}

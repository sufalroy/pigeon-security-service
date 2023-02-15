package com.skytel.pigeon.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skytel.pigeon.persistence.models.NewLocationToken;
import com.skytel.pigeon.persistence.models.UserLocation;

@Repository
public interface NewLocationTokenRepository extends JpaRepository<NewLocationToken, Long> {

    public NewLocationToken findByToken(String token);

    public NewLocationToken findByUserLocation(UserLocation userLocation);
}

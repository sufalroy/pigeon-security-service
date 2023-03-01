package com.skytel.pigeon.persistence.repositories;

import com.skytel.pigeon.persistence.entities.User;
import com.skytel.pigeon.persistence.entities.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {

    UserLocation findByCountryAndUser(String country, User user);
}

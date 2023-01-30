package com.skytel.pigeon.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skytel.pigeon.persistence.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findByEmail(String email);

    public Boolean existsByEmail(String email);

    public Boolean existsByCompany(String company);

    @Override
    public void delete(User user);
}

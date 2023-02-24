package com.skytel.pigeon.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.skytel.pigeon.persistence.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Override
    void delete(User user);
}

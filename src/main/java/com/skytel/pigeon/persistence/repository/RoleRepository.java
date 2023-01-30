package com.skytel.pigeon.persistence.repository;

import com.skytel.pigeon.persistence.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    @Override
    public void delete(Role role);
}

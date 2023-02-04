package com.skytel.pigeon.persistence.repository;

import com.skytel.pigeon.persistence.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    public Role findByName(String name);

    @Override
    public void delete(Role role);
}

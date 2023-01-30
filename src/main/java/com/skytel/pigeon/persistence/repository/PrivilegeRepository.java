package com.skytel.pigeon.persistence.repository;

import com.skytel.pigeon.persistence.models.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    public Optional<Privilege> findByName(String name);

    @Override
    public void delete(Privilege privilege);
}

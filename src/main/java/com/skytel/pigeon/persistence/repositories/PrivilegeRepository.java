package com.skytel.pigeon.persistence.repositories;

import com.skytel.pigeon.persistence.models.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    public Privilege findByName(String name);

    @Override
    public void delete(Privilege privilege);
}

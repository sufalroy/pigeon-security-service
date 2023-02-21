package com.skytel.pigeon.persistence.repositories;

import com.skytel.pigeon.persistence.models.User;
import com.skytel.pigeon.persistence.models.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.stream.Stream;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    public VerificationToken findByToken(String token);

    public VerificationToken findByUser(User user);

    public Stream<VerificationToken> findAllByExpiryDateLessThan(Date now);

    public void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query(value = "delete from VerificationToken t where t.expiryDate <= ?1", nativeQuery = true)
    public void deleteAllExpiredSince(Date now);
}

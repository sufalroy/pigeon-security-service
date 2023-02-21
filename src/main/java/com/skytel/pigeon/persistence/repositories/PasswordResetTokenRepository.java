package com.skytel.pigeon.persistence.repositories;

import java.util.Date;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.skytel.pigeon.persistence.models.PasswordResetToken;
import com.skytel.pigeon.persistence.models.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    public PasswordResetToken findByToken(String token);

    public PasswordResetToken findByUser(User user);
    
    public Stream<PasswordResetToken> findAllByExpiryDateLessThan(Date now);

    public void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query(value = "delete from PasswordResetToken t where t.expiryDate <= ?1", nativeQuery = true)
    public void deleteAllExpiredSince(Date now);
}

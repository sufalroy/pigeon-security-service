package com.skytel.pigeon.security;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skytel.pigeon.persistence.models.PasswordResetToken;
import com.skytel.pigeon.persistence.repository.PasswordResetTokenRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserSecurityService implements ISecurityUserService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public String validatePasswordResetToken(String token) {

        final PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);

        return !isTokenFound(passwordResetToken) ? "invalidToken"
                : isTokenExpired(passwordResetToken) ? "expired"
                        : null;
    }

    private boolean isTokenFound(PasswordResetToken passwordResetToken) {
        return passwordResetToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passwordResetToken) {
        
        final Calendar cal = Calendar.getInstance();
        return passwordResetToken.getExpiryDate().before(cal.getTime());
    }
}

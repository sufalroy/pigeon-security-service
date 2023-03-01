package com.skytel.pigeon.security;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skytel.pigeon.persistence.entities.PasswordResetToken;
import com.skytel.pigeon.persistence.repositories.PasswordResetTokenRepository;

import javax.transaction.Transactional;

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

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }
}

package com.skytel.pigeon;

import com.skytel.pigeon.persistence.entities.User;
import com.skytel.pigeon.persistence.entities.VerificationToken;
import com.skytel.pigeon.persistence.repositories.UserRepository;
import com.skytel.pigeon.persistence.repositories.VerificationTokenRepository;
import com.skytel.pigeon.services.PurgeTokensService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TokenExpirationImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PurgeTokensService purgeTokensService;

    @Autowired
    private PasswordEncoder encoder;

    @PersistenceContext
    private EntityManager entityManager;

    private Long tokenId;
    private Long userId;

    @BeforeEach
    public void init() {

        tokenRepository.deleteAll();

        User user = new User();
        user.setEmail(UUID.randomUUID().toString() + "@example.com");
        user.setPassword(encoder.encode(UUID.randomUUID().toString()));
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setCompany("E Corporation");
        user.setPhone("2223456780");
        user.setReference("someone");
        user.setPostal("700032");
        user.setStreet("22 B street");
        user.setState("WB");
        user.setCountry("INDIA");

        entityManager.persist(user);
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationToken.setExpiryDate(Date.from(Instant.now().minus(2, ChronoUnit.DAYS)));

        entityManager.persist(verificationToken);
        entityManager.flush();
        entityManager.clear();

        tokenId = verificationToken.getId();
        userId = user.getId();
    }

    @AfterEach
    public void flushAfter() {

        entityManager.flush();
    }

    @Test
    public void whenContextLoad_thenCorrect() {

        assertNotNull(userId);
        assertNotNull(tokenId);
        assertTrue(userRepository.findById(userId).isPresent());

        Optional<VerificationToken> verificationToken = tokenRepository.findById(tokenId);
        assertTrue(verificationToken.isPresent());
        assertTrue(tokenRepository.findAllByExpiryDateLessThan(Date.from(Instant.now()))
                .anyMatch( token -> token.equals(verificationToken.get())));
    }

    @Test
    public void whenRemoveByGeneratedQuery_thenCorrect() {
        
        tokenRepository.deleteByExpiryDateLessThan(Date.from(Instant.now()));
        assertEquals(0, tokenRepository.count());
    }

    @Test
    public void whenRemoveByJPQLQuery_thenCorrect() {

        tokenRepository.deleteAllExpiredSince(Date.from(Instant.now()));
        assertEquals(0, tokenRepository.count());
    }

    @Test
    public void whenPurgeTokenTask_thenCorrect() {

        purgeTokensService.purgeExpired();
        assertFalse(tokenRepository.findById(tokenId).isPresent());
    }
}

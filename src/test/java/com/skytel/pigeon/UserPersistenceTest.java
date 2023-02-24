package com.skytel.pigeon;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.skytel.pigeon.exceptions.EmailExistsException;
import com.skytel.pigeon.persistence.models.User;
import com.skytel.pigeon.persistence.models.VerificationToken;
import com.skytel.pigeon.persistence.repositories.UserRepository;
import com.skytel.pigeon.persistence.repositories.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@SpringBootTest
@Transactional
public class UserPersistenceTest {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @PersistenceContext
    private EntityManager entityManager;

    private Long tokenId; 
    private Long userId;

    @BeforeEach
    public void init() {

        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setCompany("E Corporation");
        user.setEmail("test@example.com");
        user.setPhone("2223456780");
        user.setPassword(encoder.encode("secure-password"));
        user.setReference("someone");
        user.setPostal("700032");
        user.setStreet("22 B street");
        user.setState("WB");
        user.setCountry("INDIA");
        entityManager.persist(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        entityManager.persist(verificationToken);

        entityManager.flush();
        entityManager.clear();

        tokenId = verificationToken.getId();
        userId = user.getId();
    }

    @AfterEach
    public void flushAfter() {

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void whenContextLoad_thenCorrect() {

        assertTrue(userRepository.findById(userId).isPresent());
        assertTrue(tokenRepository.findById(tokenId).isPresent());
    }

    @Test
    public void whenRemovingTokenThenUser_thenCorrect() {
        tokenRepository.deleteById(tokenId);
        userRepository.deleteById(userId);
    }
}

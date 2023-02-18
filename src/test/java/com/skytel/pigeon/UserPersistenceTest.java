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
import com.skytel.pigeon.persistence.repository.UserRepository;
import com.skytel.pigeon.persistence.repository.VerificationTokenRepository;

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

    @PersistenceContext
    private EntityManager entityManager;

    private Long tokenId;
    private Long userId;

    @BeforeEach
    public void givenUserAndVerificationToken() throws EmailExistsException {

        User user = new User();
        user.setFirstname("Jhon");
        user.setLastname("Doe");
        user.setCompany("E Corporation");
        user.setEmail("test@example.com");
        user.setPhone("2223456780");
        user.setPassword("secure-passwprd");
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
    public void atContextLoad() {

        assertTrue(userRepository.findById(userId).isPresent());
        assertTrue(tokenRepository.findById(tokenId).isPresent());
    }

    @Test
    public void atRemovingTokenThenUser() {
        tokenRepository.deleteById(tokenId);
        userRepository.deleteById(userId);
    }
}

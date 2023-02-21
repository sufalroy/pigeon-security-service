package com.skytel.pigeon;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.skytel.pigeon.exceptions.UserAlreadyExistException;
import com.skytel.pigeon.persistence.models.Role;
import com.skytel.pigeon.persistence.models.VerificationToken;
import com.skytel.pigeon.persistence.repositories.RoleRepository;
import com.skytel.pigeon.persistence.repositories.UserRepository;
import com.skytel.pigeon.persistence.repositories.VerificationTokenRepository;
import com.skytel.pigeon.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.skytel.pigeon.exceptions.EmailExistsException;
import com.skytel.pigeon.persistence.models.User;
import com.skytel.pigeon.services.IUserService;
import com.skytel.pigeon.web.requests.RegisterRequest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceImplTest {
    
    @Autowired
    private IUserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Test
    public void givenUserRegistration_whenRegistered_thenCorrect() throws EmailExistsException {

        final String userEmail = UUID.randomUUID().toString();
        final RegisterRequest request = createRegisterRequest(userEmail);
        
        final User user = userService.registerUser(request);

        assertNotNull(user);
        assertNotNull(user.getEmail());
        assertEquals(userEmail, user.getEmail());
        assertNotNull(user.getId());
    }

    @Test
    public void givenDetachedUser_whenAccessingEntityAssociations_thenCorrect() {

        Role role = roleRepository.findByName("ROLE_USER");
        if(role == null) {
            roleRepository.saveAndFlush(new Role("ROLE_USER"));
        }

        final User user = registerUser();

        assertNotNull(user.getRoles());

        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        assertEquals(1, roles.size());
        assertEquals("ROLE_USER", roles.iterator().next());
    }

    @Test
    public void givenDetachedUser_whenServiceLoadById_thenCorrect() throws EmailExistsException {

        final User user = registerUser();
        final User mUser = userService.getUserByID(user.getId()).get();
        assertEquals(user, mUser);
    }

    @Test
    public void givenDetachedUser_whenServiceLoadByEmail_thenCorrect() throws EmailExistsException {

        final User user = registerUser();
        final User mUser = userService.findUserByEmail(user.getEmail());
        assertEquals(user, mUser);
    }

    @Test
    public void givenUserRegistered_whenDuplicatedRegister_thenCorrect() {

        assertThrows(UserAlreadyExistException.class, () -> {

            final String email = UUID.randomUUID().toString();
            final RegisterRequest request = createRegisterRequest(email);

            userService.registerUser(request);
            userService.registerUser(request);
        });
    }

    @Transactional
    public void givenUserRegistered_whenRequestRoleAdmin_thenUserNotAdmin() {

        assertNotNull(roleRepository);
        final RegisterRequest request = new RegisterRequest();

        request.setEmail(UUID.randomUUID().toString());
        request.setPassword("SecretPassword");
        request.setMatchingPassword("SecretPassword");
        request.setFirstname("Jhon");
        request.setLastname("Doe");
        request.setCompany("E Corporation");
        request.setPhone("2221456780");
        request.setReference("someone");
        request.setPostal("700021");
        request.setCity("Kolkata");
        request.setState("WB");
        request.setCountry("India");

        assertNotNull(roleRepository.findByName("ROLE_ADMIN"));
        final Long adminRoleId = roleRepository.findByName("ROLE_ADMIN").getId();

        assertNotNull(adminRoleId);
        request.setRole(adminRoleId.intValue());

        final User user = userService.registerUser(request);
        assertFalse(user.getRoles().stream().map(Role::getId).anyMatch(ur -> ur.equals(adminRoleId)));
    }

    @Test
    public void givenUserRegistered_whenCreateToken_thenCorrect() {

        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
    }

    @Test
    public void givenUserRegistered_whenCreateTokenCreateDuplicate_thenCorrect() {

        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        userService.createVerificationTokenForUser(user, token);
    }

    @Test
    public void givenUserAndToken_whenLoadToken_thenCorrect() {

        final User user = registerUser();
        final String token = UUID.randomUUID().toString();

        userService.createVerificationTokenForUser(user, token);
        final VerificationToken verificationToken = userService.getVerificationToken(token);

        assertNotNull(verificationToken);
        assertNotNull(verificationToken.getId());
        assertNotNull(verificationToken.getUser());

        assertEquals(user, verificationToken.getUser());
        assertEquals(user.getId(), verificationToken.getUser().getId());
        assertEquals(token, verificationToken.getToken());
        assertTrue(verificationToken.getExpiryDate().toInstant().isAfter(Instant.now()));
    }

    @Test
    public void givenUserAndToken_whenRemovingUser_thenCorrect() {

        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        userService.deleteUser(user);
    }

    @Test
    public void givenUserAndToken_whenRemovingUserByDao_thenFKViolation() {

        assertThrows(DataIntegrityViolationException.class, () -> {

            final User user = registerUser();
            final String token = UUID.randomUUID().toString();
            userService.createVerificationTokenForUser(user, token);
            final long userId = user.getId();
            userService.getVerificationToken(token).getId();
            userRepository.deleteById(userId);
        });
    }

    @Test
    public void givenUserAndToken_whenRemovingTokenThenUser_thenCorrect() {

        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final long userId = user.getId();
        final long tokenId = userService.getVerificationToken(token).getId();
        tokenRepository.deleteById(tokenId);
        userRepository.deleteById(userId);
    }

    @Test
    public void givenUserAndToken_whenRemovingToken_thenCorrect() {

        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final long tokenId = userService.getVerificationToken(token).getId();
        tokenRepository.deleteById(tokenId);
    }

    @Test
    public void givenUserAndToken_whenNewTokenRequest_thenCorrect() {

        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final VerificationToken origToken = userService.getVerificationToken(token);
        final VerificationToken newToken = userService.generateNewVerificationToken(token);

        assertNotEquals(newToken.getToken(), origToken.getToken());
        assertNotEquals(newToken.getExpiryDate(), origToken.getExpiryDate());
        assertNotEquals(newToken, origToken);
    }


    @Test
    public void givenTokenValidation_whenValid_thenUserEnabled_andTokenRemoved() {

        User user = registerUser();
        final String token = UUID.randomUUID().toString();
        userService.createVerificationTokenForUser(user, token);
        final long userId = user.getId();
        final String tokenStatus = userService.validateVerificationToken(token);
        assertEquals(tokenStatus, UserService.TOKEN_VALID);
        user = userService.getUserByID(userId).get();
        assertTrue(user.isEnabled());
    }

    @Test
    public void givenTokenValidation_whenInvalid_thenCorrect() {

        final User user = registerUser();
        final String token = UUID.randomUUID().toString();
        final String invalidToken = "INVALID_" + UUID.randomUUID().toString();

        userService.createVerificationTokenForUser(user, token);
        userService.getVerificationToken(token).getId();
        final String tokenStatus = userService.validateVerificationToken(invalidToken);
        tokenStatus.equals(UserService.TOKEN_INVALID);
    }

    @Test
    public void givenTokenValidation_whenExpired_thenCorrect() {

        final User user = registerUser();
        final String token = UUID.randomUUID().toString();

        userService.createVerificationTokenForUser(user, token);
        user.getId();

        final VerificationToken verificationToken = userService.getVerificationToken(token);
        verificationToken.setExpiryDate(Date.from(verificationToken.getExpiryDate().toInstant().minus(2, ChronoUnit.DAYS)));
        tokenRepository.saveAndFlush(verificationToken);

        final String tokenStatus = userService.validateVerificationToken(token);

        assertNotNull(tokenStatus);
        tokenStatus.equals(UserService.TOKEN_EXPIRED);
    }

    private User registerUser() {

        final String email = UUID.randomUUID().toString();
        final RegisterRequest request = createRegisterRequest(email);
        final User user = userService.registerUser(request);

        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(email, user.getEmail());

        return user;
    }

    private RegisterRequest createRegisterRequest(final String email) {

        final RegisterRequest request = new RegisterRequest();

        request.setEmail(email);
        request.setPassword("SecretPassword");
        request.setMatchingPassword("SecretPassword");
        request.setFirstname("Jhon");
        request.setLastname("Doe");
        request.setCompany("E Corporation");
        request.setPhone("2221456780");
        request.setReference("someone");
        request.setPostal("700021");
        request.setCity("Kolkata");
        request.setState("WB");
        request.setCountry("India");
        request.setRole(0);

        return request;
    }
}

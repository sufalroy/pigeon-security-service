package com.skytel.pigeon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.skytel.pigeon.exceptions.EmailExistsException;
import com.skytel.pigeon.persistence.models.User;
import com.skytel.pigeon.services.IUserService;
import com.skytel.pigeon.web.requests.RegisterRequest;

@SpringBootTest
public class UserServiceIntegrationTest {
    
    @Autowired
    private IUserService userService;


    @Test
    public void testNewUserRegistration() throws EmailExistsException {

        final String userEmail = UUID.randomUUID().toString();
        final RegisterRequest request = createRegisterRequest(userEmail);
        
        final User user = userService.registerUser(request);

        assertNotNull(user);
        assertNotNull(user.getEmail());
        assertEquals(userEmail, user.getEmail());
        assertNotNull(user.getId());
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

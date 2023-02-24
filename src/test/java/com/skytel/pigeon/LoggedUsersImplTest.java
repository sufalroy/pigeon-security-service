package com.skytel.pigeon;

import com.skytel.pigeon.persistence.models.User;
import com.skytel.pigeon.persistence.repositories.UserRepository;
import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoggedUsersImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Value("${local.server.port}")
    int port;

    private FormAuthConfig formConfig;
    private String LOGGED_USERS_URL, SESSION_REGISTRY_LOGGED_USERS_URL;

    @BeforeEach
    public void init() {

        User user = userRepository.findByEmail("test@test.com");

        if(user == null) {
            user = new User();
            user.setFirstname("Test");
            user.setLastname("Test");
            user.setPassword(encoder.encode("test"));
            user.setEmail("test@test.com");
            user.setCompany("E Corporation");
            user.setPhone("2223456780");
            user.setReference("someone");
            user.setPostal("700032");
            user.setStreet("22 B street");
            user.setState("WB");
            user.setCountry("INDIA");
            user.setEnabled(true);
            userRepository.save(user);
        } else {
            user.setPassword(encoder.encode("test"));
            userRepository.save(user);
        }

        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        LOGGED_USERS_URL = "/loggedUsers";
        SESSION_REGISTRY_LOGGED_USERS_URL = "/loggedUsersFromSessionRegistry";
        formConfig = new FormAuthConfig("/login", "username", "password");
    }



    @Test
    public void givenLoggedInUser_whenGettingLoggedUsersFromActiveUserStore_thenResponseContainsUser() {

        final RequestSpecification request = RestAssured.given().auth().form("test@test.com", "test", formConfig);

        final Map<String, String> params = new HashMap<>();
        params.put("password", "test");

        final Response response = request.with().params(params).get(LOGGED_USERS_URL);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().asString().contains("test@test.com"));
    }

    @Test
    public void givenLoggedInUser_whenGettingLoggedUsersFromSessionRegistry_thenResponseContainsUser() {

        final RequestSpecification request = RestAssured.given().auth().form("test@test.com", "test", formConfig);

        final Map<String, String> params = new HashMap<>();
        params.put("password", "test");

        final Response response = request.with().params(params).get(SESSION_REGISTRY_LOGGED_USERS_URL);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().asString().contains("test@test.com"));
    }
}

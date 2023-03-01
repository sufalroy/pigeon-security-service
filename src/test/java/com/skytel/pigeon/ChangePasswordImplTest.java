package com.skytel.pigeon;

import com.skytel.pigeon.persistence.entities.User;
import com.skytel.pigeon.persistence.repositories.UserRepository;
import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChangePasswordImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Value("${local.server.port}")
    int port;

    private FormAuthConfig formConfig;
    private String URL;

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
        } else {
            user.setPassword(encoder.encode("test"));
        }

        userRepository.save(user);

        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        URL = "/user/updatePassword";
        formConfig = new FormAuthConfig("/login", "username", "password");
    }

    @AfterEach
    public void resetUserPassword() {

        final User user = userRepository.findByEmail("test@test.com");
        user.setPassword(encoder.encode("test"));
        userRepository.save(user);
    }

    @Test
    public void givenNotAuthenticatedUser_whenLoggingIn_thenCorrect() {

        final RequestSpecification request = RestAssured.given().auth().form("test@test.com", "test", formConfig);
        request.when().get("/console").then().assertThat().statusCode(200).and().body(containsString("home"));
    }

    @Test
    public void givenNotAuthenticatedUser_whenBadPasswordLoggingIn_thenCorrect() {

        final RequestSpecification request = RestAssured.given().auth().form("XXXXXXXX@XXXXXXXXX.com", "XXXXXXXX", formConfig).redirects().follow(false);
        request.when().get("/console").then().statusCode(IsNot.not(200)).body(is(emptyOrNullString()) );
    }

    @Test
    public void givenLoggedInUser_whenChangingPassword_thenCorrect() {

        final RequestSpecification request = RestAssured.given().auth().form("test@test.com", "test", formConfig);

        final Map<String, String> params = new HashMap<>();

        params.put("oldPassword", "test");
        params.put("newPassword", "newTest&12");

        final Response response = request.with().queryParams(params).post(URL);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().asString().contains("Password updated successfully"));
    }

    @Test
    public void givenWrongOldPassword_whenChangingPassword_thenBadRequest() {

        final RequestSpecification request = RestAssured.given().auth().form("test@test.com", "test", formConfig);

        final Map<String, String> params = new HashMap<>();
        params.put("oldPassword", "abc");
        params.put("newPassword", "newTest&12");

        final Response response = request.with().queryParams(params).post(URL);

        assertEquals(400, response.statusCode());
        assertTrue(response.body().asString().contains("Invalid Old Password"));
    }

    @Test
    public void givenNotAuthenticatedUser_whenChangingPassword_thenRedirect() {

        final Map<String, String> params = new HashMap<>();
        params.put("oldPassword", "abc");
        params.put("newPassword", "xyz");

        final Response response = RestAssured.with().params(params).post(URL);

        assertEquals(302, response.statusCode());
        assertFalse(response.body().asString().contains("Password updated successfully"));
    }
}

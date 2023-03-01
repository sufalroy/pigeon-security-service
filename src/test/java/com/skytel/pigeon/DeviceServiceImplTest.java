package com.skytel.pigeon;

import com.skytel.pigeon.persistence.entities.DeviceMetadata;
import com.skytel.pigeon.persistence.entities.User;
import com.skytel.pigeon.persistence.repositories.DeviceMetadataRepository;
import com.skytel.pigeon.persistence.repositories.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;

import java.util.Collections;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest(properties = "geo.ip.lib.enabled=true", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeviceServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private DeviceMetadataRepository deviceMetadataRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${local.server.port}")
    int port;

    private Long userId;

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
        user = userRepository.save(user);

        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        userId = user.getId();
    }

    @Test
    public void givenValidLoginRequest_whenNoPreviousKnownDevices_shouldSendLoginNotification() {

        final Response response = given()
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                .header("X-Forwarded-For", "122.176.98.73")
                .formParams("username", "test@test.com", "password", "test")
                .post("/login");

        assertEquals(302, response.statusCode());
        assertEquals("http://localhost:" + port + "/console", response.getHeader("Location"));
        verify(mailSender, times(0)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void givenValidLoginRequest_whenUsingKnownDevice_shouldNotSendLoginNotification() {

        DeviceMetadata existingDeviceMetadata = new DeviceMetadata();
        existingDeviceMetadata.setUserId(userId);
        existingDeviceMetadata.setLastLoggedIn(new Date());
        existingDeviceMetadata.setLocation("Kolkata");
        existingDeviceMetadata.setDeviceDetails("Chrome 71.0 - Mac OS X 10.14");
        when(deviceMetadataRepository.findByUserId(userId)).thenReturn(Collections.singletonList(existingDeviceMetadata));

        final Response response = given()
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                .header("X-Forwarded-For", "122.176.98.73")
                .formParams("username", "test@test.com", "password", "test")
                .post("/login");

        assertEquals(302, response.statusCode());
        assertEquals("http://localhost:" + port + "/console", response.getHeader("Location"));
        verify(mailSender, times(0)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void givenValidLoginRequest_whenUsingNewDevice_shouldSendLoginNotification() {

        DeviceMetadata existingDeviceMetadata = new DeviceMetadata();
        existingDeviceMetadata.setUserId(userId);
        existingDeviceMetadata.setLastLoggedIn(new Date());
        existingDeviceMetadata.setLocation("Kolkata");
        existingDeviceMetadata.setDeviceDetails("Chrome 71.0 - Mac OS X 10.4");
        when(deviceMetadataRepository.findByUserId(userId)).thenReturn(Collections.singletonList(existingDeviceMetadata));

        final Response response = given()
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                .header("X-Forwarded-For", "122.176.98.73")
                .formParams("username", "test@test.com", "password", "test")
                .post("/login");

        assertEquals(302, response.statusCode());
        assertEquals("http://localhost:" + port + "/console", response.getHeader("Location"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void givenValidLoginRequest_whenUsingKnownDeviceFromDifferentLocation_shouldSendLoginNotification() {

        DeviceMetadata existingDeviceMetadata = new DeviceMetadata();
        existingDeviceMetadata.setUserId(userId);
        existingDeviceMetadata.setLastLoggedIn(new Date());
        existingDeviceMetadata.setLocation("Kolkata");
        existingDeviceMetadata.setDeviceDetails("Chrome 71.0 - Mac OS X 10.14");
        when(deviceMetadataRepository.findByUserId(userId)).thenReturn(Collections.singletonList(existingDeviceMetadata));

        final Response response = given()
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                .header("X-Forwarded-For", "122.176.98.73")
                .formParams("username", "test@test.com", "password", "test")
                .post("/login");

        assertEquals(302, response.statusCode());
        assertEquals("http://localhost:" + port + "/console", response.getHeader("Location"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}

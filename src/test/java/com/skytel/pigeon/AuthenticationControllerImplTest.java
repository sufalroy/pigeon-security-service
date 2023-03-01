package com.skytel.pigeon;

import com.skytel.pigeon.persistence.entities.User;
import com.skytel.pigeon.persistence.entities.VerificationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class AuthenticationControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder encoder;

    private MockMvc mockMvc;
    private String token;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        User user = new User();
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

        entityManager.persist(user);
        token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        entityManager.persist(verificationToken);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void testRegistrationConfirm() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(get("/registrationConfirm?token=" + token));
        resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(model().attribute("messageKey", "message.accountVerified"));
        resultActions.andExpect(view().name("redirect:/console"));
    }

    @Test
    public void testRegistrationValidation() throws Exception {
        final MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("firstname", "");
        param.add("lastname", "");
        param.add("email", "");
        param.add("password", "");
        param.add("matchingPassword", "");
        param.add("company", "");
        param.add("phone", "");
        param.add("reference", "");
        param.add("postal", "");
        param.add("street", "");
        param.add("state", "");
        param.add("country", "");

        ResultActions resultActions = this.mockMvc.perform(post("/user/registration").params(param));
        resultActions.andExpect(status().is(400));
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("InvalidregisterRequest")))
                .andExpect(jsonPath("$.message",
                        containsString("{\"field\":\"street\",\"defaultMessage\":\"size must be between 1 and 20\"}")));
    }
}

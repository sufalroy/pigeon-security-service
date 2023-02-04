package com.skytel.pigeon.web.controllers;

import com.skytel.pigeon.persistence.models.User;
import com.skytel.pigeon.persistence.models.VerificationToken;
import com.skytel.pigeon.services.IUserService;
import com.skytel.pigeon.web.events.OnRegistrationCompleteEvent;
import com.skytel.pigeon.web.requests.RegisterRequest;
import com.skytel.pigeon.web.utility.GenericResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/user")
public class RegistrationRestController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private Environment environment;

    public RegistrationRestController() {

        super();
    }

    @PostMapping("/registration")
    public GenericResponse register(@Valid final RegisterRequest user,
            final HttpServletRequest request) {

        logger.debug("Registering user account with information: {}", user);

        final User registered = userService.registerUser(user);
        userService.addUserLocation(registered, getClentIP(request));
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getUrl(request)));

        return new GenericResponse("success");
    }

    @Async
    @GetMapping("/user/resendRegistrationToken")
    public GenericResponse resendRegistrationToken(final HttpServletRequest request,
            @RequestParam("token") final String existingToken) {

        final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        final User user = userService.getUser(newToken.getToken());
        mailSender.send(constructResendVerificationTokenEmail(getUrl(request), request.getLocale(), newToken, user));

        return new GenericResponse(messages.getMessage("message.resendToken", null, request.getLocale()));
    }

    private SimpleMailMessage constructResendVerificationTokenEmail(final String contextPath, final Locale locale,
            final VerificationToken newToken, final User user) {

        final String confirmationUrl = contextPath + "/registrationConfirm.html?token=" + newToken.getToken();
        final String message = messages.getMessage("message.resendToken", null, locale);
        
        return constructEmail("Resend Registration Token", message + " \r\n" + confirmationUrl, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(environment.getProperty("support.email"));
        
        return email;
    }

    private String getUrl(HttpServletRequest request) {

        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private String getClentIP(HttpServletRequest request) {

        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {

            return request.getRemoteAddr();
        }

        return xfHeader.split(",")[0];
    }
}

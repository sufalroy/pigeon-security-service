package com.skytel.pigeon.web.controllers;

import com.skytel.pigeon.captcha.ICaptchaService;
import com.skytel.pigeon.exceptions.InvalidOldPasswordException;
import com.skytel.pigeon.persistence.models.User;
import com.skytel.pigeon.persistence.models.VerificationToken;
import com.skytel.pigeon.security.ISecurityUserService;
import com.skytel.pigeon.services.IUserService;
import com.skytel.pigeon.web.events.OnRegistrationCompleteEvent;
import com.skytel.pigeon.web.requests.Password;
import com.skytel.pigeon.web.requests.RegisterRequest;
import com.skytel.pigeon.web.utility.GenericResponse;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class AuthenticationRestController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private ISecurityUserService securityUserService;

    @Autowired
    private ICaptchaService captchaService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private Environment environment;

    public AuthenticationRestController() {

        super();
    }

    @PostMapping("/registration")
    public GenericResponse register(@Valid final RegisterRequest user,
            final HttpServletRequest request) {

        logger.debug("Registering user account with information: {}", user);

        final String response = request.getParameter("g-recaptcha-response");
        captchaService.processResponse(response);

        final User registered = userService.registerUser(user);
        userService.addUserLocation(registered, getClientIP(request));
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getUrl(request)));

        return new GenericResponse("success");
    }

    @GetMapping("/resendRegistrationToken")
    public GenericResponse resendRegistrationToken(final HttpServletRequest request,
            @RequestParam("token") final String existingToken) {

        final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        final User user = userService.getUser(newToken.getToken());
        mailSender.send(constructResendVerificationTokenEmail(getUrl(request), request.getLocale(), newToken, user));

        return new GenericResponse(messages.getMessage("message.resendToken", null, request.getLocale()));
    }

    @PostMapping("/resetPassword")
    public GenericResponse resetPassword(final HttpServletRequest request,
            @RequestParam("email") final String userEmail) {

        final User user = userService.findUserByEmail(userEmail);
        if (user != null) {
            final String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            mailSender.send(constructResetTokenEmail(getUrl(request), request.getLocale(), token, user));
        }

        return new GenericResponse(messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
    }

    @PostMapping("/savePassword")
    public GenericResponse savePassword(final Locale locale, @Valid Password password) {

        final String result = securityUserService.validatePasswordResetToken(password.getToken());

        if (result != null) {
            return new GenericResponse(messages.getMessage("auth.message." + result, null, locale));
        }

        Optional<User> user = userService.getUserByPasswordResetToken(password.getToken());
        if (user.isPresent()) {
            userService.changeUserPassword(user.get(), password.getNewPassword());
            return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
        } else {
            return new GenericResponse(messages.getMessage("auth.message.invalid", null, locale));
        }
    }

    @PostMapping("/updatePassword")
    public GenericResponse changeUserPassword(final Locale locale, @Valid Password password) {

        final User user = userService.findUserByEmail(((User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getEmail());

        if (!userService.checkIfValidOldPassword(user, password.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }
        userService.changeUserPassword(user, password.getNewPassword());
        return new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale));
    }

    @PostMapping("/update/2fa")
    public GenericResponse modifyUser2FA(@RequestParam("use2FA") final boolean use2FA)
            throws UnsupportedEncodingException {

        final User user = userService.updateUser2FA(use2FA);
        if (use2FA) {
            return new GenericResponse(userService.generateQRUrl(user));
        }

        return null;
    }

    private SimpleMailMessage constructResendVerificationTokenEmail(final String contextPath,
                                                                    final Locale locale,
                                                                    final VerificationToken newToken,
                                                                    final User user) {

        final String confirmationUrl = contextPath + "/registrationConfirm.html?token=" + newToken.getToken();
        final String message = messages.getMessage("message.resendToken", null, locale);

        return constructEmail("Resend Registration Token", message + " \r\n" + confirmationUrl, user);
    }

    private SimpleMailMessage constructResetTokenEmail(final String contextPath,
                                                       final Locale locale,
                                                       final String token,
                                                       final User user) {

        final String url = contextPath + "/user/changePassword?token=" + token;
        final String message = messages.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
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

    private String getClientIP(HttpServletRequest request) {

        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {

            return request.getRemoteAddr();
        }

        return xfHeader.split(",")[0];
    }
}

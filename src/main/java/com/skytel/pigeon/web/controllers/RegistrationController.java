package com.skytel.pigeon.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.skytel.pigeon.services.IUserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RegistrationController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private MessageSource messages;

    public RegistrationController() {
        super();
    }

    @GetMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(final HttpServletRequest request,
            final ModelMap model, @RequestParam("token") final String token) throws UnsupportedEncodingException {


        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());

        final String result = userService.validateVerificationToken(token);

        if (result.equals("valid")) {

            model.addAttribute("messageKey", "message.accountVerified");
            return new ModelAndView("redirect:/console", model);
        }

        model.addAttribute("messageKey", "auth.message." + result);
        model.addAttribute("expired", "expired".equals(result));
        model.addAttribute("token", token);

        return new ModelAndView("redirect:/badUser", model);
    }

    @GetMapping("/console")
    public ModelAndView console(final HttpServletRequest request, final ModelMap model,
            @RequestParam("messageKey") final Optional<String> messageKey) {

        Locale locale = request.getLocale();
        messageKey.ifPresent(key -> {
            String message = messages.getMessage(key, null, locale);
            model.addAttribute("message", message);
        });

        return new ModelAndView("console", model);
    }

    @GetMapping("/badUser")
    public ModelAndView badUser(final HttpServletRequest request, final ModelMap model,
            @RequestParam("messageKey") final Optional<String> messageKey,
            @RequestParam("expired") final Optional<String> expired,
            @RequestParam("token") final Optional<String> token) {

        Locale locale = request.getLocale();
        messageKey.ifPresent(key -> {
            String message = messages.getMessage(key, null, locale);
            model.addAttribute("message", message);
        });

        expired.ifPresent(e -> model.addAttribute("expired", e));
        token.ifPresent(t -> model.addAttribute("token", t));

        return new ModelAndView("badUser", model);
    }
}

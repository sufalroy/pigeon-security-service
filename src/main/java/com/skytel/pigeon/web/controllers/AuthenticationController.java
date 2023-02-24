package com.skytel.pigeon.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.skytel.pigeon.persistence.models.Privilege;
import com.skytel.pigeon.persistence.models.Role;
import com.skytel.pigeon.persistence.models.User;
import com.skytel.pigeon.security.ISecurityUserService;
import com.skytel.pigeon.services.IUserService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthenticationController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ISecurityUserService securityUserService;

    @Autowired
    private MessageSource messages;

    public AuthenticationController() {
        super();
    }

    @GetMapping("/registrationConfirm")
    public ModelAndView confirmRegistration(final HttpServletRequest request, final ModelMap model,
            @RequestParam("token") final String token) throws UnsupportedEncodingException {

        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());
        final String result = userService.validateVerificationToken(token);

        if (result.equals("valid")) {
            final User user = userService.getUser(token);
            authWithoutPassword(user);
            model.addAttribute("messageKey", "message.accountVerified");

            return new ModelAndView("redirect:/console", model);
        }

        model.addAttribute("messageKey", "auth.message." + result);
        model.addAttribute("expired", "expired".equals(result));
        model.addAttribute("token", token);

        return new ModelAndView("redirect:/badUser", model);
    }

    @GetMapping("/console")
    public ModelAndView console(final HttpServletRequest request,
                                final ModelMap model,
                                @RequestParam("messageKey") final Optional<String> messageKey) {

        Locale locale = request.getLocale();

        messageKey.ifPresent(key -> {

            String message = messages.getMessage(key, null, locale);
            model.addAttribute("message", message);
        });

        return new ModelAndView("console", model);
    }

    @GetMapping("/badUser")
    public ModelAndView badUser(final HttpServletRequest request,
                                final ModelMap model,
                                @RequestParam("messageKey" ) final Optional<String> messageKey,
                                @RequestParam("expired" ) final Optional<String> expired,
                                @RequestParam("token" ) final Optional<String> token) {

        Locale locale = request.getLocale();

        messageKey.ifPresent(key -> {

            String message = messages.getMessage(key, null, locale);
            model.addAttribute("message", message);
        });

        expired.ifPresent(e -> model.addAttribute("expired", e));
        token.ifPresent(t -> model.addAttribute("token", t));

        return new ModelAndView("badUser", model);
    }

    @GetMapping("/user/changePassword")
    public ModelAndView showChangePasswordPage(final ModelMap model,
                                               @RequestParam("token") final String token) {

        final String result = securityUserService.validatePasswordResetToken(token);

        if(result != null) {
            String messageKey = "auth.message." + result;
            model.addAttribute("messageKey", messageKey);
            return new ModelAndView("redirect:/login", model);
        } else {
            model.addAttribute("token", token);
            return new ModelAndView("redirect:/updatePassword");
        }
    }

    @GetMapping("/updatePassword")
    public ModelAndView updatePassword(final HttpServletRequest request,
                                       final ModelMap model,
                                       @RequestParam("messageKey" ) final Optional<String> messageKey) {

        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());

        messageKey.ifPresent(key -> {

            String message = messages.getMessage(key, null, locale);
            model.addAttribute("message", message);
        });

        return new ModelAndView("updatePassword", model);
    }

    @GetMapping("/login")
    public ModelAndView login(final HttpServletRequest request,
                              final ModelMap model,
                              @RequestParam("messageKey" ) final Optional<String> messageKey,
                              @RequestParam("error" ) final Optional<String> error) {

        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());
        messageKey.ifPresent(key -> {

            String message = messages.getMessage(key, null, locale);
            model.addAttribute("message", message);
        });

        error.ifPresent(e -> model.addAttribute("error", e));

        return new ModelAndView("login", model);
    }

    @RequestMapping(value = "/user/enableNewLoc", method = RequestMethod.GET)
    public String enableNewLoc(Locale locale, Model model, @RequestParam("token") String token) {

        final String loc = userService.isValidNewLocationToken(token);

        if (loc != null) {
            model.addAttribute("message", messages.getMessage("message.newLoc.enabled", new Object[] { loc }, locale));
        } else {
            model.addAttribute("message", messages.getMessage("message.error", null, locale));
        }

        return "redirect:/login?lang=" + locale.getLanguage();
    }

    public void authWithoutPassword(User user) {

        List<Privilege> privileges = user.getRoles()
                .stream()
                .map(Role::getPrivileges)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        List<GrantedAuthority> authorities = privileges.stream()
                .map(p -> new SimpleGrantedAuthority(p.getName()))
                .collect(Collectors.toList());

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

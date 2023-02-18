package com.skytel.pigeon.web.controllers;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.skytel.pigeon.security.ActiveUserStore;
import com.skytel.pigeon.services.IUserService;

@Controller
public class UserController {

    @Autowired
    ActiveUserStore activeUserStore;

    @Autowired
    IUserService userService;

    @GetMapping("/loggedUsers")
    public String getLoggedUsers(final Locale locale, final Model model) {

        model.addAttribute("users", activeUserStore.getUsers());

        return "users";
    }

    @GetMapping("/loggedUsersFromSessionRegistry")
    public String getLoggedUsersFromSessionRegistry(final Locale locale, final Model model) {

        model.addAttribute("users", userService.getUsersFromSessionRegistry());

        return "users";
    }
}

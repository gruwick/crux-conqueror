package com.cruxconqueror.crux_conqueror.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
/**
 * Controller responsivlle for handling authentication ciews
 * 
 * Onlt handles login page
 * Authentication handle by Spring Security config
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

}

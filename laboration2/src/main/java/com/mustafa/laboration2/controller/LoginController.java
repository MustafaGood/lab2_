package com.mustafa.laboration2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Denna klass hanterar inloggningssidan
@Controller
public class LoginController {
    
    // Visar inloggningssidan när någon går till /login
    @GetMapping("/login")
    public String login() {
        return "login";  // Returnerar login.html som ska visas
    }
}
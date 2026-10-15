package com.edvora.controller;

import com.edvora.dto.RegisterDto;
import com.edvora.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerDto", new RegisterDto(null, null, null));
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registerDto") RegisterDto registerDto) {
        userService.register(registerDto);
        return "redirect:/login";
    }
}

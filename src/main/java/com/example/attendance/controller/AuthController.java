package com.example.attendance.controller;

import com.example.attendance.dto.RegistrationForm;
import com.example.attendance.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String createAccount(@Valid @ModelAttribute RegistrationForm registrationForm,
                                BindingResult bindingResult) {
        if (!bindingResult.hasFieldErrors("password")
                && !registrationForm.getPassword().equals(registrationForm.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match");
        }
        if (!bindingResult.hasFieldErrors("email") && authService.emailExists(registrationForm.getEmail())) {
            bindingResult.rejectValue("email", "email.duplicate", "An account with this email already exists");
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }

        authService.register(registrationForm);
        return "redirect:/login?registered";
    }
}
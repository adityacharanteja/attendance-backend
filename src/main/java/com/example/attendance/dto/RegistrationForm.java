package com.example.attendance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "Enter your name")
    @Size(max = 100, message = "Name must be 100 characters or fewer")
    private String fullName;

    @NotBlank(message = "Enter your email")
    @Email(message = "Enter a valid email address")
    @Size(max = 254, message = "Email must be 254 characters or fewer")
    private String email;

    @NotBlank(message = "Create a password")
    @Size(min = 8, max = 72, message = "Use a password between 8 and 72 characters")
    private String password;

    @NotBlank(message = "Confirm your password")
    private String confirmPassword;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
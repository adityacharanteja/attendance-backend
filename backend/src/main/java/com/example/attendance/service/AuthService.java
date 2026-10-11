package com.example.attendance.service;

import com.example.attendance.dto.RegistrationForm;
import com.example.attendance.entity.AuthAccount;
import com.example.attendance.repository.AuthAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService implements UserDetailsService {

    private final AuthAccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthAccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegistrationForm form) {
        String email = normalizeEmail(form.getEmail());
        if (accountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        AuthAccount account = new AuthAccount(
                form.getFullName().trim(),
                email,
                passwordEncoder.encode(form.getPassword())
        );
        accountRepository.save(account);
    }

    public boolean emailExists(String email) {
        return accountRepository.existsByEmail(normalizeEmail(email));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthAccount account = accountRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        return User.withUsername(account.getEmail())
                .password(account.getPasswordHash())
                .roles("USER")
                .build();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
package com.example.attendance;

import com.example.attendance.dto.RegistrationForm;
import com.example.attendance.entity.AuthAccount;
import com.example.attendance.repository.AuthAccountRepository;
import com.example.attendance.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Test
    void shouldNormalizeEmailAndHashPasswordWhenRegistering() {
        AuthAccountRepository repository = mock(AuthAccountRepository.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        AuthService service = new AuthService(repository, encoder);
        RegistrationForm form = new RegistrationForm();
        form.setFullName("  Alex Rivera  ");
        form.setEmail(" ALEX@example.com ");
        form.setPassword("secure-pass-123");

        when(repository.existsByEmail("alex@example.com")).thenReturn(false);

        service.register(form);

        verify(repository).save(any(AuthAccount.class));
        org.mockito.ArgumentCaptor<AuthAccount> accountCaptor =
                org.mockito.ArgumentCaptor.forClass(AuthAccount.class);
        verify(repository).save(accountCaptor.capture());
        AuthAccount account = accountCaptor.getValue();
        assertEquals("Alex Rivera", account.getFullName());
        assertEquals("alex@example.com", account.getEmail());
        assertTrue(encoder.matches("secure-pass-123", account.getPasswordHash()));
        assertTrue(!account.getPasswordHash().equals("secure-pass-123"));
    }

    @Test
    void shouldRejectAnExistingEmail() {
        AuthAccountRepository repository = mock(AuthAccountRepository.class);
        AuthService service = new AuthService(repository, new BCryptPasswordEncoder());
        RegistrationForm form = new RegistrationForm();
        form.setFullName("Alex Rivera");
        form.setEmail("alex@example.com");
        form.setPassword("secure-pass-123");

        when(repository.existsByEmail("alex@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.register(form));
        verify(repository, never()).save(any(AuthAccount.class));
    }
}
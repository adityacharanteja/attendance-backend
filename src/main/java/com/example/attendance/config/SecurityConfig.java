package com.example.attendance.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

import java.util.function.Supplier;

@Configuration
public class SecurityConfig {

    @Bean
        SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        @Value("${server.servlet.session.cookie.secure:false}") boolean secureCookies) throws Exception {
                CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
                csrfTokenRepository.setCookieCustomizer(cookie -> cookie.secure(secureCookies).sameSite("Lax"));

        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/error",
                                "/api/auth/csrf", "/api/auth/register", "/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        request -> request.getServletPath().startsWith("/api/")))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/api/auth/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessUrl("/login?logout")
                )
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

        private final CsrfTokenRequestHandler plainHandler = new CsrfTokenRequestAttributeHandler();
        private final CsrfTokenRequestHandler xorHandler = new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                           Supplier<CsrfToken> deferredCsrfToken) {
            xorHandler.handle(request, response, deferredCsrfToken);
            deferredCsrfToken.get();
        }

        @Override
        public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
            String headerValue = request.getHeader(csrfToken.getHeaderName());
            CsrfTokenRequestHandler handler = headerValue == null ? xorHandler : plainHandler;
            return handler.resolveCsrfTokenValue(request, csrfToken);
        }
    }
}
package com.project.msvc_auth.config;

import com.project.msvc_auth.model.Auth;
import com.project.msvc_auth.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInicialConfig {

    @Bean
    public CommandLineRunner crearAdminInicial(
            AuthRepository authRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.username}") String username,
            @Value("${app.admin.password}") String password) {

        return args -> {

            if (username.isBlank() || password.isBlank()) {
                return;
            }

            if (authRepository.findByUsername(username).isPresent()) {
                return;
            }

            Auth admin = new Auth();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRol("ADMIN");

            authRepository.save(admin);
        };
    }
}

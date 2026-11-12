package com.project.msvc_auth.service;

import com.project.msvc_auth.model.Auth;
import com.project.msvc_auth.repository.AuthRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Auth> listarAuth() {

        logger.info("Listando usuarios auth");

        return authRepository.findAll();
    }

    public Auth guardarAuth(Auth auth) {

        logger.info("Guardando auth: " + auth.getUsername());

        auth.setPassword(passwordEncoder.encode(auth.getPassword()));

        return authRepository.save(auth);
    }

    public Auth buscarPorId(Long id) {

        logger.info("Buscando auth con id: " + id);

        return authRepository.findById(id).orElse(null);
    }

    public Auth actualizarAuth(Long id, Auth auth) {

        logger.info("Actualizando auth con id: " + id);

        Auth authExistente = authRepository.findById(id).orElse(null);

        if (authExistente != null) {

            authExistente.setUsername(auth.getUsername());
            authExistente.setPassword(passwordEncoder.encode(auth.getPassword()));
            authExistente.setRol(auth.getRol());

            return authRepository.save(authExistente);
        }

        return null;
    }

    public void eliminarAuth(Long id) {

        logger.info("Eliminando auth con id: " + id);

        authRepository.deleteById(id);
    }
}
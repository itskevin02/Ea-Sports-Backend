package com.project.msvc_auth.service;

import com.project.msvc_auth.dto.RegistroCuentaSolicitud;
import com.project.msvc_auth.model.Auth;
import com.project.msvc_auth.repository.AuthRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthService.class);

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthRepository authRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Auth> listarAuth() {

        logger.info("Listando usuarios auth");

        return authRepository.findAll();
    }

    public Auth guardarAuth(Auth auth) {

        logger.info("Guardando auth: " + auth.getUsername());

        auth.setPassword(
                passwordEncoder.encode(auth.getPassword())
        );

        return authRepository.save(auth);
    }

    public Auth registrarCuenta(RegistroCuentaSolicitud datos) {

        String username = datos.username().trim();

        if (!username.matches("[a-zA-Z0-9_]{4,30}")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El usuario solo puede contener letras, números y guion bajo"
            );
        }

        if (authRepository.existsByUsernameIgnoreCase(username)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nombre de usuario ya está registrado"
            );
        }

        Auth cuenta = new Auth();

        cuenta.setUsername(username);

        cuenta.setPassword(
                passwordEncoder.encode(datos.password())
        );

        cuenta.setRol("USUARIO");

        logger.info("Registrando nueva cuenta: " + username);

        return authRepository.save(cuenta);
    }

    public Auth buscarPorId(Long id) {

        logger.info("Buscando auth con id: " + id);

        return authRepository.findById(id).orElse(null);
    }

    public Auth actualizarAuth(Long id, Auth auth) {

        logger.info("Actualizando auth con id: " + id);

        Auth authExistente =
                authRepository.findById(id).orElse(null);

        if (authExistente != null) {

            authExistente.setUsername(auth.getUsername());

            authExistente.setPassword(
                    passwordEncoder.encode(auth.getPassword())
            );

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

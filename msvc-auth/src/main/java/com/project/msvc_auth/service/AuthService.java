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

    private static final String ADMIN_PRINCIPAL = "adminLocal";

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

        if (authRepository.existsByUsernameIgnoreCase(
                auth.getUsername().trim()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nombre de usuario ya existe"
            );
        }

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
                    "El usuario solo puede contener letras, numeros y guion bajo"
            );
        }

        if (username.equalsIgnoreCase(ADMIN_PRINCIPAL)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nombre de usuario no esta disponible"
            );
        }

        if (authRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nombre de usuario ya esta registrado"
            );
        }

        Auth cuenta = new Auth();

        cuenta.setUsername(username);
        cuenta.setPassword(
                passwordEncoder.encode(datos.password())
        );
        cuenta.setRol("USUARIO");

        logger.info("Registrando nueva cuenta: {}", username);

        return authRepository.save(cuenta);
    }

    public Auth buscarPorId(Long id) {
        return authRepository.findById(id).orElse(null);
    }

    public Auth actualizarAuth(Long id, Auth auth) {

        Auth existente = authRepository.findById(id)
                .orElse(null);

        if (existente == null) {
            return null;
        }

        if (existente.getUsername()
                .equalsIgnoreCase(ADMIN_PRINCIPAL)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No se puede modificar la cuenta administrativa principal"
            );
        }

        if (auth.getUsername()
                .equalsIgnoreCase(ADMIN_PRINCIPAL)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nombre de usuario esta reservado"
            );
        }

        existente.setUsername(auth.getUsername());
        existente.setPassword(
                passwordEncoder.encode(auth.getPassword())
        );
        existente.setRol(auth.getRol());

        return authRepository.save(existente);
    }

    public void eliminarAuth(Long id) {

        Auth existente = authRepository.findById(id)
                .orElse(null);

        if (existente == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La cuenta no existe"
            );
        }

        if (existente.getUsername()
                .equalsIgnoreCase(ADMIN_PRINCIPAL)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No se puede eliminar la cuenta administrativa principal"
            );
        }

        authRepository.deleteById(id);
    }
}

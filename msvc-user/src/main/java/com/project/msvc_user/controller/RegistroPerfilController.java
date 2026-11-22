package com.project.msvc_user.controller;

import com.project.msvc_user.dto.RegistroPerfilSolicitud;
import com.project.msvc_user.model.Usuario;
import com.project.msvc_user.service.JwtServicio;
import com.project.msvc_user.service.RegistroPerfilService;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
public class RegistroPerfilController {

    private final RegistroPerfilService registroPerfilService;
    private final JwtServicio jwtServicio;

    public RegistroPerfilController(
            RegistroPerfilService registroPerfilService,
            JwtServicio jwtServicio
    ) {
        this.registroPerfilService = registroPerfilService;
        this.jwtServicio = jwtServicio;
    }

    @PostMapping("/registro-perfil")
    public ResponseEntity<Usuario> registrar(
            @Valid @RequestBody RegistroPerfilSolicitud datos,
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization
    ) {

        if (!authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token no valido"
            );
        }

        Object identificador = jwtServicio.validar(
                authorization.substring(7)
        ).get("authId");

        if (!(identificador instanceof Number)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "El token no contiene authId"
            );
        }

        Long authId = ((Number) identificador).longValue();

        Usuario perfil = registroPerfilService.registrar(
                datos,
                authId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(perfil);
    }
}

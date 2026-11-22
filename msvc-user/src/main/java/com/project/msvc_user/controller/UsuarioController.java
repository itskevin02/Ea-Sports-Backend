package com.project.msvc_user.controller;

import com.project.msvc_user.model.Usuario;
import com.project.msvc_user.service.JwtServicio;
import com.project.msvc_user.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtServicio jwtServicio;

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {

        return ResponseEntity.ok(
                usuarioService.listarUsuarios()
        );
    }

    @PostMapping
    public ResponseEntity<Usuario> guardar(
            @Valid @RequestBody Usuario usuario
    ) {

        Usuario guardado = usuarioService.guardarUsuario(usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardado);
    }

    @GetMapping("/mi-perfil")
    public ResponseEntity<Usuario> miPerfil(
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization
    ) {

        if (!authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token no valido"
            );
        }

        Object dato = jwtServicio
                .validar(authorization.substring(7))
                .get("authId");

        if (!(dato instanceof Number)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "La sesion no contiene authId"
            );
        }

        Long authId = ((Number) dato).longValue();

        Usuario usuario = usuarioService.buscarPorAuthId(authId);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}/vincular-auth/{authId}")
    public ResponseEntity<Usuario> vincularCuenta(
            @PathVariable Long id,
            @PathVariable Long authId,
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization
    ) {

        Usuario usuario = usuarioService.vincularCuenta(
                id,
                authId,
                authorization
        );

        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(
            @PathVariable Long id
    ) {

        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Usuario usuario
    ) {

        Usuario actualizado =
                usuarioService.actualizarUsuario(id, usuario);

        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization
    ) {

        usuarioService.eliminarUsuario(
                id,
                authorization
        );

        return ResponseEntity.noContent().build();
    }
}
package com.project.msvc_user.controller;

import com.project.msvc_user.model.Usuario;
import com.project.msvc_user.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

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
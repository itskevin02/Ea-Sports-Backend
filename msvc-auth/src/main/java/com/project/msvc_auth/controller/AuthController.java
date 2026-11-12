package com.project.msvc_auth.controller;

import com.project.msvc_auth.dto.LoginSolicitud;
import com.project.msvc_auth.dto.LoginRespuesta;
import com.project.msvc_auth.dto.UsuarioDTO;
import com.project.msvc_auth.exception.CredencialesException;
import com.project.msvc_auth.model.Auth;
import com.project.msvc_auth.repository.AuthRepository;
import com.project.msvc_auth.service.AuthService;
import com.project.msvc_auth.service.JwtServicio;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServicio jwtServicio;

    public AuthController(
            AuthService authService,
            AuthRepository authRepository,
            PasswordEncoder passwordEncoder,
            JwtServicio jwtServicio) {

        this.authService = authService;
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtServicio = jwtServicio;
    }

    @PostMapping("/login")
    public LoginRespuesta login(
            @Valid @RequestBody LoginSolicitud datos) {

        Auth usuario = authRepository.findByUsername(datos.username())
                .filter(auth -> passwordEncoder.matches(
                        datos.password(), auth.getPassword()))
                .orElseThrow(() -> new CredencialesException(
                        "Usuario o contraseña incorrectos"));

        String token = jwtServicio.generar(
                usuario.getUsername(),
                usuario.getRol()
        );

        return new LoginRespuesta(
                token,
                new UsuarioDTO(
                        usuario.getUsername(),
                        usuario.getRol()
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<Auth>> listar() {

        List<Auth> lista = authService.listarAuth();

        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<Auth> guardar(
            @Valid @RequestBody Auth auth) {

        Auth guardado = authService.guardarAuth(auth);

        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auth> buscarPorId(
            @PathVariable Long id) {

        Auth auth = authService.buscarPorId(id);

        if (auth != null) {
            return ResponseEntity.ok(auth);
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Auth> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Auth auth) {

        Auth actualizado = authService.actualizarAuth(id, auth);

        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        authService.eliminarAuth(id);

        return ResponseEntity.noContent().build();
    }
}
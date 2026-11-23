package com.project.msvc_registration.controller;

import com.project.msvc_registration.dto.TeamDTO;
import com.project.msvc_registration.model.Registration;
import com.project.msvc_registration.service.JwtAcceso;
import com.project.msvc_registration.service.RegistrationService;

import io.jsonwebtoken.Claims;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private JwtAcceso jwtAcceso;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<List<Registration>> listar() {

        return ResponseEntity.ok(
                registrationService.listarRegistrations()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Registration> buscarPorId(
            @PathVariable Long id
    ) {

        Registration registro =
                registrationService.buscarPorId(id);

        if (registro == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(registro);
    }

    private void comprobarEquipo(
            Long equipoId,
            Claims datos
    ) {

        if (jwtAcceso.esAdmin(datos)) {
            return;
        }

        TeamDTO equipo;

        try {

            equipo = restTemplate.getForObject(
                    "http://localhost:8084/teams/" + equipoId,
                    TeamDTO.class
            );

        } catch (RestClientException e) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo verificar el propietario del equipo"
            );
        }

        if (equipo == null) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El equipo no existe"
            );
        }

        jwtAcceso.comprobarPropietario(
                datos,
                equipo.getAuthId()
        );
    }

    @PostMapping
    public ResponseEntity<Registration> guardar(
            @Valid @RequestBody Registration registration,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {

        Claims datos = jwtAcceso.validar(authorization);

        comprobarEquipo(
                registration.getEquipoId(),
                datos
        );

        registration.setId(null);

        if (!jwtAcceso.esAdmin(datos)) {
            registration.setEstado("ACTIVO");
        }

        Registration guardado =
                registrationService.guardarRegistration(
                        registration
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Registration> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Registration registration,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {

        Claims datos = jwtAcceso.validar(authorization);

        Registration existente =
                registrationService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        comprobarEquipo(
                existente.getEquipoId(),
                datos
        );

        if (!jwtAcceso.esAdmin(datos)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el administrador puede actualizar inscripciones"
            );
        }

        Registration actualizado =
                registrationService.actualizarRegistration(
                        id,
                        registration
                );

        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {

        Claims datos = jwtAcceso.validar(authorization);

        Registration existente =
                registrationService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        comprobarEquipo(
                existente.getEquipoId(),
                datos
        );

        registrationService.eliminarRegistration(id);

        return ResponseEntity.noContent().build();
    }
}

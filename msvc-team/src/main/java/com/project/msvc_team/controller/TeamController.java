package com.project.msvc_team.controller;

import com.project.msvc_team.model.Team;
import com.project.msvc_team.service.JwtAcceso;
import com.project.msvc_team.service.TeamService;

import io.jsonwebtoken.Claims;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private JwtAcceso jwtAcceso;

    @GetMapping
    public ResponseEntity<List<Team>> listar() {

        return ResponseEntity.ok(
                teamService.listarTeams()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> buscarPorId(
            @PathVariable Long id
    ) {

        Team team = teamService.buscarPorId(id);

        if (team == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(team);
    }

    @PostMapping
    public ResponseEntity<Team> guardar(
            @Valid @RequestBody Team team,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {

        Claims datos = jwtAcceso.validar(authorization);

        team.setId(null);

        if (jwtAcceso.esAdmin(datos)) {

            team.setAuthId(null);

        } else {

            team.setAuthId(jwtAcceso.authId(datos));
        }

        Team guardado = teamService.guardarTeam(team);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Team team,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {

        Claims datos = jwtAcceso.validar(authorization);

        Team existente = teamService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        jwtAcceso.comprobarPropietario(
                datos,
                existente.getAuthId()
        );

        Team actualizado = teamService.actualizarTeam(
                id,
                team
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

        Team existente = teamService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        jwtAcceso.comprobarPropietario(
                datos,
                existente.getAuthId()
        );

        teamService.eliminarTeam(id);

        return ResponseEntity.noContent().build();
    }
}

package com.project.msvc_match.controller;

import com.project.msvc_match.model.Match;
import com.project.msvc_match.service.JwtAcceso;
import com.project.msvc_match.service.MatchService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private JwtAcceso jwtAcceso;

    @GetMapping
    public ResponseEntity<List<Match>> listar() {

        return ResponseEntity.ok(
                matchService.listarMatches()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> buscarPorId(
            @PathVariable Long id
    ) {

        Match match = matchService.buscarPorId(id);

        if (match == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(match);
    }

    @PostMapping
    public ResponseEntity<Match> guardar(
            @Valid @RequestBody Match match,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {

        jwtAcceso.exigirOrganizador(authorization);

        match.setId(null);

        Match guardado = matchService.guardarMatch(match);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Match match,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {

        jwtAcceso.exigirOrganizador(authorization);

        Match actualizado = matchService.actualizarMatch(
                id,
                match
        );

        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }

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

        jwtAcceso.exigirOrganizador(authorization);

        Match existente = matchService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        matchService.eliminarMatch(id);

        return ResponseEntity.noContent().build();
    }
}
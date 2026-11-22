package com.project.msvc_sanction.controller;

import com.project.msvc_sanction.model.Sanction;
import com.project.msvc_sanction.service.SanctionService;
import com.project.msvc_sanction.service.UsuarioValidador;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sanctions")
public class SanctionController {

    @Autowired
    private SanctionService sanctionService;

    @Autowired
    private UsuarioValidador usuarioValidador;

    @GetMapping
    public ResponseEntity<List<Sanction>> listar() {

        return ResponseEntity.ok(
                sanctionService.listarSanctions()
        );
    }

    @PostMapping
    public ResponseEntity<Sanction> guardar(
            @Valid @RequestBody Sanction sanction,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {

        usuarioValidador.validar(
                sanction.getUsuarioId(),
                authorization
        );

        Sanction guardada =
                sanctionService.guardarSanction(sanction);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sanction> buscarPorId(
            @PathVariable Long id
    ) {

        Sanction sanction = sanctionService.buscarPorId(id);

        if (sanction != null) {
            return ResponseEntity.ok(sanction);
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sanction> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Sanction sanction,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {

        if (sanctionService.buscarPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }

        usuarioValidador.validar(
                sanction.getUsuarioId(),
                authorization
        );

        Sanction actualizada =
                sanctionService.actualizarSanction(id, sanction);

        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {

        sanctionService.eliminarSanction(id);

        return ResponseEntity.noContent().build();
    }
}
package com.project.msvc_prize.controller;

import com.project.msvc_prize.model.Prize;
import com.project.msvc_prize.service.JwtAcceso;
import com.project.msvc_prize.service.PrizeService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prizes")
public class PrizeController {

    @Autowired
    private PrizeService prizeService;

    @Autowired
    private JwtAcceso jwtAcceso;

    @GetMapping
    public ResponseEntity<List<Prize>> listar() {

        return ResponseEntity.ok(
                prizeService.listarPrizes()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prize> buscarPorId(
            @PathVariable Long id
    ) {

        Prize prize = prizeService.buscarPorId(id);

        if (prize == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(prize);
    }

    @PostMapping
    public ResponseEntity<Prize> guardar(
            @Valid @RequestBody Prize prize,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {

        jwtAcceso.exigirOrganizador(authorization);

        prize.setId(null);

        Prize guardado = prizeService.guardarPrize(prize);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prize> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Prize prize,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {

        jwtAcceso.exigirOrganizador(authorization);

        Prize actualizado = prizeService.actualizarPrize(
                id,
                prize
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

        Prize existente = prizeService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        prizeService.eliminarPrize(id);

        return ResponseEntity.noContent().build();
    }
}

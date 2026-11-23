package com.project.msvc_result.controller;

import com.project.msvc_result.model.Result;
import com.project.msvc_result.service.JwtAcceso;
import com.project.msvc_result.service.ResultService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private JwtAcceso jwtAcceso;

    @GetMapping
    public ResponseEntity<List<Result>> listar() {

        return ResponseEntity.ok(
                resultService.listarResults()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result> buscarPorId(
            @PathVariable Long id
    ) {

        Result result = resultService.buscarPorId(id);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Result> guardar(
            @Valid @RequestBody Result result,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {

        jwtAcceso.exigirOrganizador(authorization);

        result.setId(null);

        Result guardado = resultService.guardarResult(result);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Result result,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {

        jwtAcceso.exigirOrganizador(authorization);

        Result actualizado = resultService.actualizarResult(
                id,
                result
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

        Result existente = resultService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        resultService.eliminarResult(id);

        return ResponseEntity.noContent().build();
    }
}

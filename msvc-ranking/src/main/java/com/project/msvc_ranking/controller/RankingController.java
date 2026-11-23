package com.project.msvc_ranking.controller;

import com.project.msvc_ranking.model.Ranking;
import com.project.msvc_ranking.service.JwtAcceso;
import com.project.msvc_ranking.service.RankingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rankings")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @Autowired
    private JwtAcceso jwtAcceso;

    @GetMapping
    public ResponseEntity<List<Ranking>> listar() {

        return ResponseEntity.ok(
                rankingService.listarRankings()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ranking> buscarPorId(
            @PathVariable Long id
    ) {

        Ranking ranking = rankingService.buscarPorId(id);

        if (ranking == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ranking);
    }

    @PostMapping
    public ResponseEntity<Ranking> guardar(
            @Valid @RequestBody Ranking ranking,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {

        jwtAcceso.exigirOrganizador(authorization);

        ranking.setId(null);

        Ranking guardado = rankingService.guardarRanking(ranking);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ranking> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Ranking ranking,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorization
    ) {

        jwtAcceso.exigirOrganizador(authorization);

        Ranking actualizado = rankingService.actualizarRanking(
                id,
                ranking
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

        Ranking existente = rankingService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        rankingService.eliminarRanking(id);

        return ResponseEntity.noContent().build();
    }
}
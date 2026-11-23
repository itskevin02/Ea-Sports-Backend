package com.project.msvc_result.service;

import com.project.msvc_result.model.Result;
import com.project.msvc_result.repository.ResultRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ResultService {

    private static final Logger logger =
            LoggerFactory.getLogger(ResultService.class);

    @Autowired
    private ResultRepository resultRepository;

    public List<Result> listarResults() {

        logger.info("Listando resultados");

        return resultRepository.findAll();
    }

    public Result buscarPorId(Long id) {

        logger.info("Buscando resultado con id: {}", id);

        return resultRepository.findById(id).orElse(null);
    }

    private void validarPuntajes(Result result) {

        if (result.getPuntajeA() == null ||
                result.getPuntajeB() == null) {

            throw new RuntimeException(
                    "Los puntajes son obligatorios"
            );
        }

        if (result.getPuntajeA() < 0 ||
                result.getPuntajeB() < 0) {

            throw new RuntimeException(
                    "Los puntajes no pueden ser negativos"
            );
        }
    }

    public Result guardarResult(Result result) {

        validarPuntajes(result);

        if (resultRepository.existsByPartidaId(
                result.getPartidaId()
        )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta partida ya tiene un resultado registrado"
            );
        }

        logger.info("Guardando resultado");
        return resultRepository.save(result);
    }

    public Result actualizarResult(Long id, Result result) {

        Result existente = resultRepository
                .findById(id)
                .orElse(null);

        if (existente == null) {
            return null;
        }

        if ("VALIDADO".equalsIgnoreCase(
                existente.getEstadoValidacion()
        )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede modificar un resultado validado"
            );
        }

        validarPuntajes(result);

        if (resultRepository.existsByPartidaIdAndIdNot(
                result.getPartidaId(),
                id
        )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta partida ya tiene otro resultado"
            );
        }

        existente.setPartidaId(result.getPartidaId());
        existente.setGanadorId(result.getGanadorId());
        existente.setPuntajeA(result.getPuntajeA());
        existente.setPuntajeB(result.getPuntajeB());
        existente.setEstadoValidacion(
                result.getEstadoValidacion()
        );

        logger.info("Actualizando resultado con id: {}", id);

        return resultRepository.save(existente);
    }

    public void eliminarResult(Long id) {

        Result existente = resultRepository
                .findById(id)
                .orElse(null);

        if (existente != null &&
                "VALIDADO".equalsIgnoreCase(
                        existente.getEstadoValidacion()
                )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar un resultado validado"
            );
        }

        logger.info("Eliminando resultado con id: {}", id);

        resultRepository.deleteById(id);
    }
}

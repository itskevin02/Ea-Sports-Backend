package com.project.msvc_result.service;

import com.project.msvc_result.model.Result;
import com.project.msvc_result.repository.ResultRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResultServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private ResultService resultService;

    @Test
    void listarResultsTest() {

        Result result = new Result();
        result.setPartidaId(1L);

        when(resultRepository.findAll())
                .thenReturn(Arrays.asList(result));

        assertEquals(
                1,
                resultService.listarResults().size()
        );

        verify(resultRepository, times(1)).findAll();
    }

    @Test
    void buscarResultPorIdTest() {

        Result result = new Result();
        result.setId(1L);
        result.setPartidaId(1L);

        when(resultRepository.findById(1L))
                .thenReturn(Optional.of(result));

        Result resultado = resultService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getPartidaId());

        verify(resultRepository, times(1)).findById(1L);
    }

    @Test
    void guardarResultTest() {

        Result result = new Result();
        result.setPartidaId(1L);
        result.setGanadorId(2L);
        result.setPuntajeA(3);
        result.setPuntajeB(1);
        result.setEstadoValidacion("Validado");

        when(resultRepository.save(result))
                .thenReturn(result);

        Result resultado = resultService.guardarResult(result);

        assertNotNull(resultado);
        assertEquals(3, resultado.getPuntajeA());

        verify(resultRepository, times(1)).save(result);
    }

    @Test
    void guardarResultConPuntajeNegativoTest() {

        Result result = new Result();
        result.setPartidaId(1L);
        result.setGanadorId(2L);
        result.setPuntajeA(-1);
        result.setPuntajeB(2);
        result.setEstadoValidacion("Validado");

        RuntimeException error = assertThrows(
                RuntimeException.class,
                () -> resultService.guardarResult(result)
        );

        assertEquals(
                "Los puntajes no pueden ser negativos",
                error.getMessage()
        );

        verify(resultRepository, never())
                .save(any(Result.class));
    }

    @Test
    void actualizarResultTest() {

        Result existente = new Result();
        existente.setId(1L);
        existente.setPartidaId(1L);
        existente.setGanadorId(2L);
        existente.setPuntajeA(2);
        existente.setPuntajeB(1);
        existente.setEstadoValidacion("Pendiente");

        Result nuevo = new Result();
        nuevo.setPartidaId(2L);
        nuevo.setGanadorId(3L);
        nuevo.setPuntajeA(5);
        nuevo.setPuntajeB(4);
        nuevo.setEstadoValidacion("Validado");

        when(resultRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(resultRepository.save(any(Result.class)))
                .thenReturn(existente);

        Result resultado =
                resultService.actualizarResult(1L, nuevo);

        assertNotNull(resultado);
        assertEquals(2L, resultado.getPartidaId());
        assertEquals(3L, resultado.getGanadorId());
        assertEquals(5, resultado.getPuntajeA());
        assertEquals(4, resultado.getPuntajeB());
        assertEquals("Validado", resultado.getEstadoValidacion());

        verify(resultRepository, times(1))
                .save(any(Result.class));
    }

    @Test
    void actualizarResultConPuntajeNegativoTest() {

        Result existente = new Result();
        existente.setId(1L);
        existente.setPuntajeA(2);
        existente.setPuntajeB(1);

        Result nuevo = new Result();
        nuevo.setPartidaId(2L);
        nuevo.setGanadorId(3L);
        nuevo.setPuntajeA(-5);
        nuevo.setPuntajeB(4);
        nuevo.setEstadoValidacion("Validado");

        when(resultRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        RuntimeException error = assertThrows(
                RuntimeException.class,
                () -> resultService.actualizarResult(1L, nuevo)
        );

        assertEquals(
                "Los puntajes no pueden ser negativos",
                error.getMessage()
        );

        verify(resultRepository, never())
                .save(any(Result.class));
    }

    @Test
    void eliminarResultTest() {

        resultService.eliminarResult(1L);

        verify(resultRepository, times(1))
                .deleteById(1L);
    }

    @Test
    void noPermitirResultadoDuplicadoTest() {

        Result result = new Result();
        result.setPartidaId(1L);
        result.setGanadorId(3L);
        result.setPuntajeA(2);
        result.setPuntajeB(1);
        result.setEstadoValidacion("PENDIENTE");

        when(resultRepository.existsByPartidaId(1L))
                .thenReturn(true);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> resultService.guardarResult(result)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                error.getStatusCode()
        );

        verify(resultRepository, never())
                .save(any(Result.class));
    }

    @Test
    void noModificarResultadoValidadoTest() {

        Result existente = new Result();
        existente.setId(1L);
        existente.setEstadoValidacion("VALIDADO");

        Result nuevo = new Result();
        nuevo.setPartidaId(1L);
        nuevo.setGanadorId(3L);
        nuevo.setPuntajeA(5);
        nuevo.setPuntajeB(0);
        nuevo.setEstadoValidacion("VALIDADO");

        when(resultRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> resultService.actualizarResult(1L, nuevo)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                error.getStatusCode()
        );

        verify(resultRepository, never())
                .save(any(Result.class));
    }

    @Test
    void noEliminarResultadoValidadoTest() {

        Result existente = new Result();
        existente.setId(1L);
        existente.setEstadoValidacion("VALIDADO");

        when(resultRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> resultService.eliminarResult(1L)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                error.getStatusCode()
        );

        verify(resultRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void noAsignarPartidaConOtroResultadoTest() {

        Result existente = new Result();
        existente.setId(1L);
        existente.setPartidaId(1L);
        existente.setEstadoValidacion("PENDIENTE");

        Result nuevo = new Result();
        nuevo.setPartidaId(2L);
        nuevo.setGanadorId(3L);
        nuevo.setPuntajeA(2);
        nuevo.setPuntajeB(1);
        nuevo.setEstadoValidacion("PENDIENTE");

        when(resultRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(resultRepository.existsByPartidaIdAndIdNot(
                2L, 1L
        )).thenReturn(true);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> resultService.actualizarResult(1L, nuevo)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                error.getStatusCode()
        );

        verify(resultRepository, never())
                .save(any(Result.class));
    }
}

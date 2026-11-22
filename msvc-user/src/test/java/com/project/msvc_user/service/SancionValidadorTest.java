package com.project.msvc_user.service;

import com.project.msvc_user.dto.SancionDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SancionValidadorTest {

    @Mock
    private RestTemplate restTemplate;

    private SancionValidador validador;

    @BeforeEach
    void preparar() {

        validador = new SancionValidador(
                restTemplate,
                "http://localhost:8089"
        );
    }

    @Test
    void permiteUsuarioSinSanciones() {

        when(restTemplate.exchange(
                eq("http://localhost:8089/sanctions"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(SancionDTO[].class)
        )).thenReturn(
                ResponseEntity.ok(new SancionDTO[0])
        );

        assertDoesNotThrow(
                () -> validador.validarSinSanciones(
                        2L,
                        "Bearer prueba"
                )
        );
    }

    @Test
    void rechazaUsuarioConSanciones() {

        SancionDTO sancion = new SancionDTO();
        sancion.setUsuarioId(2L);

        when(restTemplate.exchange(
                eq("http://localhost:8089/sanctions"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(SancionDTO[].class)
        )).thenReturn(
                ResponseEntity.ok(
                        new SancionDTO[]{sancion}
                )
        );

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> validador.validarSinSanciones(
                        2L,
                        "Bearer prueba"
                )
        );

        assertEquals(
                HttpStatus.CONFLICT,
                error.getStatusCode()
        );
    }

    @Test
    void rechazaSiSanctionNoEstaDisponible() {

        when(restTemplate.exchange(
                eq("http://localhost:8089/sanctions"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(SancionDTO[].class)
        )).thenThrow(
                new ResourceAccessException("Servicio apagado")
        );

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> validador.validarSinSanciones(
                        2L,
                        "Bearer prueba"
                )
        );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                error.getStatusCode()
        );
    }
}

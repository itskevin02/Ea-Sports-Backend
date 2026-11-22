package com.project.msvc_sanction.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioValidadorTest {

    @Mock
    private RestTemplate restTemplate;

    private UsuarioValidador validador;

    @BeforeEach
    void preparar() {
        validador = new UsuarioValidador(
                restTemplate,
                "http://localhost:8083"
        );
    }

    @Test
    void permiteUsuarioExistente() {

        when(restTemplate.exchange(
                eq("http://localhost:8083/users/2"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(ResponseEntity.ok("{}"));

        assertDoesNotThrow(
                () -> validador.validar(2L, "Bearer prueba")
        );
    }

    @Test
    void rechazaUsuarioInexistente() {

        when(restTemplate.exchange(
                eq("http://localhost:8083/users/999"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(
                new HttpClientErrorException(HttpStatus.NOT_FOUND)
        );

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> validador.validar(999L, "Bearer prueba")
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                error.getStatusCode()
        );
    }

    @Test
    void rechazaCuandoUserNoEstaDisponible() {

        when(restTemplate.exchange(
                eq("http://localhost:8083/users/2"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(
                new ResourceAccessException("Servicio apagado")
        );

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> validador.validar(2L, "Bearer prueba")
        );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                error.getStatusCode()
        );
    }
}

package com.project.msvc_user.service;

import com.project.msvc_user.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioEliminacionTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SancionValidador sancionValidador;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void rechazaEliminarUsuarioInexistente() {

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.eliminarUsuario(
                        999L,
                        "Bearer prueba"
                )
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                error.getStatusCode()
        );

        verify(usuarioRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void rechazaEliminarUsuarioConSanciones() {

        when(usuarioRepository.existsById(2L))
                .thenReturn(true);

        doThrow(new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Usuario con sanciones"
        )).when(sancionValidador)
                .validarSinSanciones(2L, "Bearer prueba");

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.eliminarUsuario(
                        2L,
                        "Bearer prueba"
                )
        );

        assertEquals(
                HttpStatus.CONFLICT,
                error.getStatusCode()
        );

        verify(usuarioRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void rechazaEliminarSiSanctionNoResponde() {

        when(usuarioRepository.existsById(3L))
                .thenReturn(true);

        doThrow(new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Sanction no disponible"
        )).when(sancionValidador)
                .validarSinSanciones(3L, "Bearer prueba");

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.eliminarUsuario(
                        3L,
                        "Bearer prueba"
                )
        );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                error.getStatusCode()
        );

        verify(usuarioRepository, never())
                .deleteById(anyLong());
    }
}

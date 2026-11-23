package com.project.msvc_game.service;

import com.project.msvc_game.model.Juego;
import com.project.msvc_game.repository.JuegoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JuegoServiceTest {

    @Mock
    private JuegoRepository juegoRepository;

    @InjectMocks
    private JuegoService juegoService;

    @Test
    void listarJuegosTest() {

        Juego juego = new Juego();
        juego.setNombre("Juego 1");

        when(juegoRepository.findAll())
                .thenReturn(Arrays.asList(juego));

        assertEquals(1, juegoService.listarJuegos().size());

        verify(juegoRepository, times(1)).findAll();
    }

    @Test
    void buscarJuegoPorIdTest() {

        Juego juego = new Juego();
        juego.setId(1L);
        juego.setNombre("Juego 1");

        when(juegoRepository.findById(1L))
                .thenReturn(Optional.of(juego));

        Juego resultado = juegoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Juego 1", resultado.getNombre());

        verify(juegoRepository, times(1)).findById(1L);
    }

    @Test
    void guardarJuegoTest() {

        Juego juego = new Juego();
        juego.setNombre("Juego Nuevo");

        when(juegoRepository.save(juego))
                .thenReturn(juego);

        Juego resultado = juegoService.guardarJuego(juego);

        assertNotNull(resultado);
        assertEquals("Juego Nuevo", resultado.getNombre());

        verify(juegoRepository, times(1)).save(juego);
    }

    @Test
    void actualizarJuegoTest() {

        Juego juegoExistente = new Juego();
        juegoExistente.setId(1L);
        juegoExistente.setNombre("Juego 1");
        juegoExistente.setGenero("Genero 1");
        juegoExistente.setModalidad("Online");
        juegoExistente.setJugadoresPorEquipo(5);
        juegoExistente.setEstado("Activo");

        Juego nuevoJuego = new Juego();
        nuevoJuego.setNombre("Juego Actualizado");
        nuevoJuego.setGenero("Genero 2");
        nuevoJuego.setModalidad("Presencial");
        nuevoJuego.setJugadoresPorEquipo(10);
        nuevoJuego.setEstado("Inactivo");
        nuevoJuego.setImagen("https://ejemplo.com/juego.jpg");

        when(juegoRepository.findById(1L))
                .thenReturn(Optional.of(juegoExistente));

        when(juegoRepository.save(any(Juego.class)))
                .thenReturn(juegoExistente);

        Juego resultado =
                juegoService.actualizarJuego(1L, nuevoJuego);

        assertNotNull(resultado);
        assertEquals("Juego Actualizado", resultado.getNombre());
        assertEquals("Genero 2", resultado.getGenero());
        assertEquals("Presencial", resultado.getModalidad());
        assertEquals(10, resultado.getJugadoresPorEquipo());
        assertEquals("Inactivo", resultado.getEstado());

        assertEquals(
                "https://ejemplo.com/juego.jpg",
                resultado.getImagen()
        );

        verify(juegoRepository, times(1))
                .findById(1L);

        verify(juegoRepository, times(1))
                .save(any(Juego.class));
    }

    @Test
    void eliminarJuegoTest() {

        juegoService.eliminarJuego(1L);

        verify(juegoRepository, times(1)).deleteById(1L);
    }

    @Test
    void guardarJuegoConImagenTest() {

        Juego juego = new Juego();
        juego.setNombre("Juego con imagen");
        juego.setImagen("https://ejemplo.com/imagen.jpg");

        when(juegoRepository.save(juego))
                .thenReturn(juego);

        Juego resultado = juegoService.guardarJuego(juego);

        assertEquals(
                "https://ejemplo.com/imagen.jpg",
                resultado.getImagen()
        );

        verify(juegoRepository, times(1)).save(juego);
    }
}

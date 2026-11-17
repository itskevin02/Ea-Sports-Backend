
package com.project.msvc_tournament.service;

import com.project.msvc_tournament.model.Tournament;
import com.project.msvc_tournament.repository.TournamentRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentService tournamentService;

    @Test
    void listarTournamentsTest() {

        Tournament tournament = new Tournament();
        tournament.setNombre("Torneo 1");

        when(tournamentRepository.findAll())
                .thenReturn(Arrays.asList(tournament));

        assertEquals(1, tournamentService.listarTournaments().size());

        verify(tournamentRepository, times(1)).findAll();
    }

    @Test
    void buscarTournamentPorIdTest() {

        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setNombre("Torneo 1");

        when(tournamentRepository.findById(1L))
                .thenReturn(Optional.of(tournament));

        Tournament resultado = tournamentService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Torneo 1", resultado.getNombre());

        verify(tournamentRepository, times(1)).findById(1L);
    }

    @Test
    void guardarTournamentTest() {

        Tournament tournament = new Tournament();
        tournament.setNombre("Nuevo Torneo");

        when(tournamentRepository.save(tournament))
                .thenReturn(tournament);

        Tournament resultado =
                tournamentService.guardarTournament(tournament);

        assertNotNull(resultado);
        assertEquals("Nuevo Torneo", resultado.getNombre());

        verify(tournamentRepository, times(1)).save(tournament);
    }

    @Test
    void actualizarTournamentTest() {

        Tournament tournamentExistente = new Tournament();
        tournamentExistente.setId(1L);
        tournamentExistente.setNombre("Torneo 1");
        tournamentExistente.setUbicacion("Ubicación 1");
        tournamentExistente.setCantidadEquipos(8);
        tournamentExistente.setEstado("Activo");

        Tournament nuevoTournament = new Tournament();
        nuevoTournament.setNombre("Torneo Actualizado");
        nuevoTournament.setUbicacion("Ubicación 2");
        nuevoTournament.setCantidadEquipos(16);
        nuevoTournament.setEstado("Inactivo");

        when(tournamentRepository.findById(1L))
                .thenReturn(Optional.of(tournamentExistente));

        when(tournamentRepository.save(any(Tournament.class)))
                .thenReturn(tournamentExistente);

        Tournament resultado =
                tournamentService.actualizarTournament(
                        1L, nuevoTournament
                );

        assertNotNull(resultado);
        assertEquals("Torneo Actualizado", resultado.getNombre());
        assertEquals("Ubicación 2", resultado.getUbicacion());
        assertEquals(16, resultado.getCantidadEquipos());
        assertEquals("Inactivo", resultado.getEstado());

        verify(tournamentRepository, times(1)).findById(1L);
        verify(tournamentRepository, times(1))
                .save(any(Tournament.class));
    }

    @Test
    void eliminarTournamentTest() {

        tournamentService.eliminarTournament(1L);

        verify(tournamentRepository, times(1)).deleteById(1L);
    }

    @Test
    void guardarConFechasInvalidasTest() {

        Tournament tournament = new Tournament();

        tournament.setNombre("Torneo de Prueba");
        tournament.setFechaInicio(LocalDate.of(2026, 11, 15));
        tournament.setFechaCierre(LocalDate.of(2026, 11, 20));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> tournamentService.guardarTournament(tournament)
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());

        verify(tournamentRepository, never())
                .save(any(Tournament.class));
    }

    @Test
    void actualizarCamposNuevosTest() {

        Tournament existente = new Tournament();
        existente.setId(1L);

        Tournament nuevo = new Tournament();

        nuevo.setNombre("Copa eSports");
        nuevo.setUbicacion("Santiago");
        nuevo.setCantidadEquipos(16);
        nuevo.setEstado("Abierto");

        nuevo.setJuegoId(2L);
        nuevo.setPlataforma("PC");
        nuevo.setFechaInicio(LocalDate.of(2026, 12, 20));
        nuevo.setFechaCierre(LocalDate.of(2026, 12, 15));
        nuevo.setDescripcion("Torneo competitivo");
        nuevo.setImagen("torneo.jpg");

        when(tournamentRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(tournamentRepository.save(existente))
                .thenReturn(existente);

        Tournament resultado =
                tournamentService.actualizarTournament(1L, nuevo);

        assertEquals(2L, resultado.getJuegoId());
        assertEquals("PC", resultado.getPlataforma());
        assertEquals(LocalDate.of(2026, 12, 20),
                resultado.getFechaInicio());
        assertEquals(LocalDate.of(2026, 12, 15),
                resultado.getFechaCierre());
        assertEquals("Torneo competitivo",
                resultado.getDescripcion());
        assertEquals("torneo.jpg", resultado.getImagen());

        verify(tournamentRepository, times(1)).save(existente);
    }

    @Test
    void actualizarConFechasInvalidasTest() {

        Tournament existente = new Tournament();
        existente.setId(1L);

        Tournament nuevo = new Tournament();

        nuevo.setFechaInicio(LocalDate.of(2026, 12, 10));
        nuevo.setFechaCierre(LocalDate.of(2026, 12, 15));

        when(tournamentRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> tournamentService.actualizarTournament(1L, nuevo)
        );

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());

        verify(tournamentRepository, never())
                .save(any(Tournament.class));
    }
}

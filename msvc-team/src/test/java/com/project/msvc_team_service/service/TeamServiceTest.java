package com.project.msvc_team.service;

import com.project.msvc_team.dto.RegistrationDTO;
import com.project.msvc_team.model.Team;
import com.project.msvc_team.repository.TeamRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TeamService teamService;

    @Test
    void listarTeamsTest() {

        Team team = new Team();
        team.setNombre("Equipo 1");

        when(teamRepository.findAll())
                .thenReturn(Arrays.asList(team));

        assertEquals(1, teamService.listarTeams().size());

        verify(teamRepository, times(1)).findAll();
    }

    @Test
    void buscarTeamPorIdTest() {

        Team team = new Team();
        team.setId(1L);
        team.setNombre("Equipo 1");

        when(teamRepository.findById(1L))
                .thenReturn(Optional.of(team));

        Team resultado = teamService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Equipo 1", resultado.getNombre());

        verify(teamRepository, times(1)).findById(1L);
    }

    @Test
    void guardarTeamTest() {

        Team team = new Team();
        team.setNombre("Equipo Nuevo");

        when(teamRepository.save(team)).thenReturn(team);

        Team resultado = teamService.guardarTeam(team);

        assertNotNull(resultado);
        assertEquals("Equipo Nuevo", resultado.getNombre());

        verify(teamRepository, times(1)).save(team);
    }

    @Test
    void actualizarTeamTest() {

        Team teamExistente = new Team();
        teamExistente.setId(1L);
        teamExistente.setNombre("Equipo 1");
        teamExistente.setCoach("Coach 1");
        teamExistente.setJuegoId(1L);
        teamExistente.setEstado("Activo");

        Team nuevoTeam = new Team();
        nuevoTeam.setNombre("Equipo Actualizado");
        nuevoTeam.setCoach("Coach 2");
        nuevoTeam.setJuegoId(2L);
        nuevoTeam.setEstado("Inactivo");

        when(teamRepository.findById(1L))
                .thenReturn(Optional.of(teamExistente));

        when(teamRepository.save(any(Team.class)))
                .thenReturn(teamExistente);

        Team resultado =
                teamService.actualizarTeam(1L, nuevoTeam);

        assertNotNull(resultado);
        assertEquals("Equipo Actualizado", resultado.getNombre());
        assertEquals("Coach 2", resultado.getCoach());
        assertEquals(2L, resultado.getJuegoId());
        assertEquals("Inactivo", resultado.getEstado());

        verify(teamRepository, times(1)).findById(1L);
        verify(teamRepository, times(1))
                .save(any(Team.class));
    }

    @Test
    void eliminarTeamTest() {

        when(teamRepository.existsById(1L))
                .thenReturn(true);

        when(restTemplate.getForObject(
                "http://localhost:8086/registrations",
                RegistrationDTO[].class
        )).thenReturn(new RegistrationDTO[0]);

        teamService.eliminarTeam(1L);

        verify(teamRepository, times(1))
                .deleteById(1L);
    }

    @Test
    void noEliminarEquipoInscritoTest() {

        RegistrationDTO inscripcion = new RegistrationDTO();
        inscripcion.setId(1L);
        inscripcion.setEquipoId(1L);
        inscripcion.setTorneoId(1L);

        when(teamRepository.existsById(1L))
                .thenReturn(true);

        when(restTemplate.getForObject(
                "http://localhost:8086/registrations",
                RegistrationDTO[].class
        )).thenReturn(new RegistrationDTO[]{inscripcion});

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> teamService.eliminarTeam(1L)
        );

        assertEquals(
                HttpStatus.CONFLICT,
                error.getStatusCode()
        );

        verify(teamRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void noEliminarSiRegistrationNoRespondeTest() {

        when(teamRepository.existsById(1L))
                .thenReturn(true);

        when(restTemplate.getForObject(
                "http://localhost:8086/registrations",
                RegistrationDTO[].class
        )).thenThrow(
                new ResourceAccessException("Sin conexion")
        );

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> teamService.eliminarTeam(1L)
        );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                error.getStatusCode()
        );

        verify(teamRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void noEliminarEquipoInexistenteTest() {

        when(teamRepository.existsById(99L))
                .thenReturn(false);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> teamService.eliminarTeam(99L)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                error.getStatusCode()
        );

        verifyNoInteractions(restTemplate);

        verify(teamRepository, never())
                .deleteById(anyLong());
    }

    @Test
    void noEliminarSiRespuestaEsNulaTest() {

        when(teamRepository.existsById(1L))
                .thenReturn(true);

        when(restTemplate.getForObject(
                "http://localhost:8086/registrations",
                RegistrationDTO[].class
        )).thenReturn(null);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> teamService.eliminarTeam(1L)
        );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                error.getStatusCode()
        );

        verify(teamRepository, never())
                .deleteById(anyLong());
    }
}

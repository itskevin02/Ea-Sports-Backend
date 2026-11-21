package com.project.msvc_team.service;

import com.project.msvc_team.dto.RegistrationDTO;
import com.project.msvc_team.model.Team;
import com.project.msvc_team.repository.TeamRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class TeamService {

    private static final Logger logger =
            LoggerFactory.getLogger(TeamService.class);

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<Team> listarTeams() {

        logger.info("Listando equipos");

        return teamRepository.findAll();
    }

    public Team guardarTeam(Team team) {

        logger.info("Guardando equipo: " + team.getNombre());

        return teamRepository.save(team);
    }

    public Team buscarPorId(Long id) {

        logger.info("Buscando equipo con id: " + id);

        return teamRepository.findById(id).orElse(null);
    }

    public Team actualizarTeam(Long id, Team team) {

        logger.info("Actualizando equipo con id: " + id);

        Team teamExistente =
                teamRepository.findById(id).orElse(null);

        if (teamExistente != null) {

            teamExistente.setNombre(team.getNombre());
            teamExistente.setCoach(team.getCoach());
            teamExistente.setJuegoId(team.getJuegoId());
            teamExistente.setEstado(team.getEstado());

            return teamRepository.save(teamExistente);
        }

        return null;
    }

    public void eliminarTeam(Long id) {

        logger.info("Validando eliminacion del equipo: " + id);

        // Comprobar que el equipo existe
        if (!teamRepository.existsById(id)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El equipo no existe"
            );
        }

        RegistrationDTO[] inscripciones;

        // Consultar las inscripciones del servidor
        try {

            inscripciones = restTemplate.getForObject(
                    "http://localhost:8086/registrations",
                    RegistrationDTO[].class
            );

        } catch (RestClientException e) {

            logger.error(
                    "No se pudo consultar registration-service",
                    e
            );

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo consultar el servicio de inscripciones"
            );
        }

        // Evitar eliminar si no se pudo obtener la respuesta
        if (inscripciones == null) {

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se recibieron las inscripciones del servidor"
            );
        }

        // Revisar si el equipo tiene inscripciones
        for (RegistrationDTO inscripcion : inscripciones) {

            if (inscripcion != null &&
                    id.equals(inscripcion.getEquipoId())) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "No se puede eliminar el equipo porque tiene inscripciones"
                );
            }
        }

        // Eliminar solamente si no tiene inscripciones
        logger.info("Eliminando equipo con id: " + id);

        teamRepository.deleteById(id);
    }
}

package com.project.msvc_registration.service;

import com.project.msvc_registration.dto.TeamDTO;
import com.project.msvc_registration.dto.TournamentDTO;
import com.project.msvc_registration.model.Registration;
import com.project.msvc_registration.repository.RegistrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RegistrationService {

    private static final Logger logger =
            LoggerFactory.getLogger(RegistrationService.class);

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.team.url:http://localhost:8084}")
    private String teamUrl;

    @Value("${app.tournament.url:http://localhost:8085}")
    private String tournamentUrl;

    public List<Registration> listarRegistrations() {

        logger.info("Listando inscripciones");

        return registrationRepository.findAll();
    }

    public Registration guardarRegistration(Registration registration) {

        logger.info("Validando equipo y torneo");

        TeamDTO team;

        try {

            team = restTemplate.getForObject(
                    teamUrl + "/teams/" +
                            registration.getEquipoId(),
                    TeamDTO.class
            );

        } catch (Exception e) {

            logger.error("Error al consumir team-service");

            throw new RuntimeException(
                    "Error al consumir team-service"
            );
        }

        TournamentDTO tournament;

        try {

            tournament = restTemplate.getForObject(
                    tournamentUrl + "/tournaments/" +
                            registration.getTorneoId(),
                    TournamentDTO.class
            );

        } catch (Exception e) {

            logger.error("Error al consumir tournament-service");

            throw new RuntimeException(
                    "Error al consumir tournament-service"
            );
        }

        if (team == null) {

            throw new RuntimeException(
                    "El equipo no existe"
            );
        }

        if (tournament == null) {

            throw new RuntimeException(
                    "El torneo no existe"
            );
        }

        if (team.getEstado() != null &&
                !team.getEstado().equalsIgnoreCase("ACTIVO")) {

            throw new RuntimeException(
                    "El equipo no estÃ¡ activo"
            );
        }

         if (tournament.getEstado() != null &&
                 !tournament.getEstado().equalsIgnoreCase("ACTIVO") &&
                 !tournament.getEstado().equalsIgnoreCase("Abierto")) {

        throw new RuntimeException(
                        "El torneo no estÃ¡ abierto para inscripciones"
         );
         }


        long inscritos =
                registrationRepository.countByTorneoId(
                        registration.getTorneoId()
                );

        if (tournament.getCantidadEquipos() != null &&
                inscritos >= tournament.getCantidadEquipos()) {

            throw new RuntimeException(
                    "El torneo ya alcanzÃ³ el mÃ¡ximo de equipos"
            );
        }

        Registration existente =
                registrationRepository.findByEquipoIdAndTorneoId(
                        registration.getEquipoId(),
                        registration.getTorneoId()
                );

        if (existente != null) {

            throw new RuntimeException(
                    "El equipo ya estÃ¡ inscrito en este torneo"
            );
        }

        logger.info("Guardando inscripciÃ³n");

        return registrationRepository.save(registration);
    }

    public Registration buscarPorId(Long id) {

        logger.info("Buscando inscripciÃ³n con id: {}", id);

        return registrationRepository.findById(id).orElse(null);
    }

    public Registration actualizarRegistration(
            Long id,
            Registration registration
    ) {

        logger.info("Actualizando inscripciÃ³n con id: {}", id);

        Registration registrationExistente =
                registrationRepository.findById(id).orElse(null);

        if (registrationExistente != null) {

            registrationExistente.setEquipoId(
                    registration.getEquipoId()
            );

            registrationExistente.setTorneoId(
                    registration.getTorneoId()
            );

            registrationExistente.setEstado(
                    registration.getEstado()
            );

            return registrationRepository.save(
                    registrationExistente
            );
        }

        return null;
    }

    public void eliminarRegistration(Long id) {

        logger.info("Eliminando inscripciÃ³n con id: {}", id);

        registrationRepository.deleteById(id);
    }
}

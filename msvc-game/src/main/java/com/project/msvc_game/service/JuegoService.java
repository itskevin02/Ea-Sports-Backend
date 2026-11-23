package com.project.msvc_game.service;

import com.project.msvc_game.model.Juego;
import com.project.msvc_game.repository.JuegoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class JuegoService {

    private static final Logger logger =
            LoggerFactory.getLogger(JuegoService.class);

    @Autowired
    private JuegoRepository juegoRepository;

    public List<Juego> listarJuegos() {

        logger.info("Listando juegos");

        return juegoRepository.findAll();
    }

    public Juego guardarJuego(Juego juego) {

        logger.info("Guardando juego: " + juego.getNombre());

        return juegoRepository.save(juego);
    }

    public Juego buscarPorId(Long id) {

        logger.info("Buscando juego con id: " + id);

        return juegoRepository.findById(id).orElse(null);
    }

    public Juego actualizarJuego(Long id, Juego juego) {

        logger.info("Actualizando juego con id: " + id);

        Juego juegoExistente =
                juegoRepository.findById(id).orElse(null);

        if (juegoExistente != null) {

            juegoExistente.setNombre(juego.getNombre());
            juegoExistente.setGenero(juego.getGenero());
            juegoExistente.setModalidad(juego.getModalidad());

            juegoExistente.setJugadoresPorEquipo(
                    juego.getJugadoresPorEquipo()
            );

            juegoExistente.setEstado(juego.getEstado());

            juegoExistente.setImagen(juego.getImagen());

            return juegoRepository.save(juegoExistente);
        }

        return null;
    }

    public void eliminarJuego(Long id) {

        logger.info("Eliminando juego con id: " + id);

        juegoRepository.deleteById(id);
    }
}

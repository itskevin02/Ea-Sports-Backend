package com.project.msvc_ranking.service;

import com.project.msvc_ranking.model.Ranking;
import com.project.msvc_ranking.repository.RankingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class RankingService {

    private static final Logger logger =
            LoggerFactory.getLogger(RankingService.class);

    @Autowired
    private RankingRepository rankingRepository;

    public List<Ranking> listarRankings() {

        logger.info("Listando rankings");

        return rankingRepository.findAll();
    }

    public Ranking guardarRanking(Ranking ranking) {

        logger.info(
                "Guardando ranking: {}",
                ranking.getNombreEquipo()
        );

        return rankingRepository.save(ranking);
    }

    public Ranking buscarPorId(Long id) {

        logger.info("Buscando ranking con id: {}", id);

        return rankingRepository.findById(id).orElse(null);
    }

    public Ranking actualizarRanking(Long id, Ranking ranking) {

        Ranking existente = rankingRepository
                .findById(id)
                .orElse(null);

        if (existente == null) {
            return null;
        }

        existente.setTorneoId(ranking.getTorneoId());
        existente.setNombreEquipo(ranking.getNombreEquipo());
        existente.setPuntos(ranking.getPuntos());
        existente.setPosicion(ranking.getPosicion());
        existente.setEstado(ranking.getEstado());

        logger.info("Actualizando ranking con id: {}", id);

        return rankingRepository.save(existente);
    }

    public void eliminarRanking(Long id) {

        logger.info("Eliminando ranking con id: {}", id);

        rankingRepository.deleteById(id);
    }
}

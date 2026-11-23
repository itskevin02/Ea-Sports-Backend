package com.project.msvc_result.repository;

import com.project.msvc_result.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultRepository
        extends JpaRepository<Result, Long> {

    // Comprobar si una partida ya tiene resultado
    boolean existsByPartidaId(Long partidaId);

    // Comprobar duplicados al modificar un resultado
    boolean existsByPartidaIdAndIdNot(
            Long partidaId,
            Long id
    );
}

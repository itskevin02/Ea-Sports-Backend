package com.project.msvc_sanction.repository;

import com.project.msvc_sanction.model.Sanction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SanctionRepository extends JpaRepository<Sanction, Long> {

    boolean existsByUsuarioIdAndEstadoIgnoreCase(
            Long usuarioId,
            String estado
    );

    boolean existsByUsuarioIdAndEstadoIgnoreCaseAndIdNot(
            Long usuarioId,
            String estado,
            Long id
    );
}

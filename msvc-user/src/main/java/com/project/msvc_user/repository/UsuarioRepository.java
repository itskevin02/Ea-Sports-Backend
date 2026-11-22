package com.project.msvc_user.repository;

import com.project.msvc_user.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByAuthId(Long authId);

    boolean existsByNicknameIgnoreCase(String nickname);

    boolean existsByCorreoIgnoreCase(String correo);
}
